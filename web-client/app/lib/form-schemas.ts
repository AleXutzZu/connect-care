import {z} from "zod";

export const donorFormSchema = z.object({
    firstName: z.string()
        .min(1, "First name cannot be empty")
        .max(50, "First name is too long"),
    lastName: z.string()
        .min(1, "Last name cannot be empty")
        .max(50, "Last name is too long"),
    address: z.string()
        .min(1, "Address cannot be empty")
        .max(100, "Address is too long"),
    phoneNumber: z.string()
        .regex(/\d{10}/, "Invalid phone number")
});

export type DonorFormSchema = z.infer<typeof donorFormSchema>;