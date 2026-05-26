import {z} from "zod";

export const donorFormSchema = z.object({
    firstName: z.string("You must provide a first name")
        .min(1, "First name cannot be empty")
        .max(50, "First name is too long"),
    lastName: z.string("You must provide a last name")
        .min(1, "Last name cannot be empty")
        .max(50, "Last name is too long"),
    address: z.string("You must provide an address")
        .min(1, "Address cannot be empty")
        .max(100, "Address is too long"),
    phoneNumber: z.string("You must provide a phone number")
        .regex(/\d{10}/, "Invalid phone number")
});

export type DonorFormSchema = z.infer<typeof donorFormSchema>;

export const donationFormSchema = z.object({
    donorId: z.number("You must select a donor"),
    charityId: z.number("You must select a charity"),
    amount: z.number("Amount must be a number").positive("Amount must be positive")
})

export type DonationFormSchema = z.infer<typeof donationFormSchema>;