import os
import psycopg2
from faker import Faker
import random
from datetime import datetime, timedelta
from psycopg2.extras import execute_values, execute_batch
import time

DB_CONFIG = {
    "dbname": os.environ.get("DB_NAME", "teledon"),
    "user": os.environ.get("DB_USER", "user"),
    "password": os.environ.get("DB_PASS", "password"),
    "host": os.environ.get("DB_HOST", "localhost"),
    "port": os.environ.get("DB_PORT", "5432")
}

fake = Faker()


def wait_for_db():
    print("Waiting for database connection and schema...")
    retries = 30
    while retries > 0:
        try:
            conn = psycopg2.connect(**DB_CONFIG)
            cur = conn.cursor()
            cur.execute("SELECT to_regclass('public.charity');")
            if cur.fetchone()[0] is not None:
                conn.close()
                print("Database and tables are ready!")
                return
            conn.close()
        except psycopg2.OperationalError:
            pass
        print("Database not ready yet, waiting 2 seconds...")
        time.sleep(2)
        retries -= 1
    raise Exception("Database failed to initialize in time.")


def seed_database(target_charities=100, target_donors=2000, target_donations=5000):
    conn = None
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cur = conn.cursor()

        cur.execute("SELECT id FROM Charity")
        charity_ids = [row[0] for row in cur.fetchall()]

        charities_to_make = target_charities - len(charity_ids)
        if charities_to_make > 0:
            print(f"Generating {charities_to_make} charities...")
            for _ in range(charities_to_make):
                cur.execute(
                    "INSERT INTO Charity (name, target, user_id) VALUES (%s, %s, %s) RETURNING id",
                    (fake.company() + " Foundation", random.randint(10000, 250000), 1)
                )
                charity_ids.append(cur.fetchone()[0])

        cur.execute("SELECT id FROM Donor")
        donor_ids = [row[0] for row in cur.fetchall()]

        donors_to_make = target_donors - len(donor_ids)
        if donors_to_make > 0:
            print(f"Generating {donors_to_make} donors (this might take a few seconds)...")
            donor_data = []
            for _ in range(donors_to_make):
                donor_data.append((
                    fake.first_name(),
                    fake.last_name(),
                    fake.phone_number()[:15],  # Truncated in case of strict DB limits
                    fake.address().replace('\n', ', '),
                    datetime.now()
                ))

            execute_values(cur,
                           "INSERT INTO Donor (firstName, lastName, phoneNumber, address, createdon) VALUES %s",
                           donor_data
                           )
            cur.execute("SELECT id FROM Donor")
            donor_ids = [row[0] for row in cur.fetchall()]

        print(f"Generating {target_donations} historical donations...")
        donation_data = []

        now = datetime.now()
        start_date = now - timedelta(days=540)
        total_seconds_range = int((now - start_date).total_seconds())

        for _ in range(target_donations):
            d_id = random.choice(donor_ids)
            c_id = random.choice(charity_ids)

            probability = random.random()
            if probability < 0.90:
                amount = round(random.uniform(5, 50), 2)
            elif probability < 0.98:
                amount = round(random.uniform(51, 150), 2)
            else:
                amount = round(random.uniform(151, 1000), 2)

            random_offset = random.randint(0, total_seconds_range)
            created_at = start_date + timedelta(seconds=random_offset)

            donation_data.append((d_id, c_id, amount, created_at))

        donation_data.sort(key=lambda x: x[3])

        execute_values(cur,
                       "INSERT INTO Donation (donorid, charityid, amount, createdon) VALUES %s",
                       donation_data,
                       page_size=1000
                       )

        conn.commit()
        print("Success! Database seeded with realistic data.")

    except Exception as e:
        print(f"Error: {e}")
        if conn:
            conn.rollback()
    finally:
        if conn:
            cur.close()
            conn.close()


def backfill_registration_dates():
    conn = None
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        cur = conn.cursor()

        print("Fetching donor history to calculate logical registration dates...")

        cur.execute("""
                    SELECT d.id, MIN(don.createdon)
                    FROM Donor d
                             LEFT JOIN Donation don ON d.id = don.donorid
                    GROUP BY d.id
                    """)

        donor_records = cur.fetchall()
        now = datetime.now()
        update_data = []

        for donor_id, first_donation_date in donor_records:
            if first_donation_date:
                days_before = random.randint(1, 180)
                seconds_before = random.randint(0, 86400)  # Randomize the time of day
                reg_date = first_donation_date - timedelta(days=days_before, seconds=seconds_before)
            else:
                days_ago = random.randint(1, 365)
                seconds_ago = random.randint(0, 86400)
                reg_date = now - timedelta(days=days_ago, seconds=seconds_ago)

            update_data.append((reg_date, donor_id))

        print(f"Updating {len(update_data)} donors...")

        execute_batch(cur,
                      "UPDATE Donor SET createdon = %s WHERE id = %s",
                      update_data,
                      page_size=1000  # Process in chunks of 1000 for speed
                      )

        conn.commit()
        print("Success! All donors now have logical registration dates.")

    except Exception as e:
        print(f"Error: {e}")
        if conn:
            conn.rollback()
    finally:
        if conn:
            cur.close()
            conn.close()


if __name__ == "__main__":
    wait_for_db()
    seed_database(target_charities=120, target_donors=2500, target_donations=5500)
    backfill_registration_dates()
