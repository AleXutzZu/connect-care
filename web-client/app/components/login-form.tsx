import {cn} from "~/lib/utils"
import {Button} from "~/components/ui/button"
import {Card, CardContent, CardDescription, CardHeader, CardTitle,} from "~/components/ui/card"
import {Field, FieldError, FieldGroup, FieldLabel,} from "~/components/ui/field"
import {Input} from "~/components/ui/input"
import * as z from "zod";
import {Controller, useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import React, {useCallback, useEffect, useState} from "react";
import {useFetcher} from "react-router";
import {Alert, AlertDescription, AlertTitle} from "~/components/ui/alert";
import {AlertCircleIcon} from "lucide-react";

const formSchema = z.object({
    username: z.string().min(1, "Username cannot be empty"),
    password: z.string().min(1, "Password cannot be empty")
});

type LoginForm = z.infer<typeof formSchema>;

export function LoginForm({className, ...props}: React.ComponentProps<"div">) {
    const form = useForm<LoginForm>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            username: "",
            password: ""
        },
        mode: "onChange"
    });

    const fetcher = useFetcher();
    const [showAuthAlert, setShowAuthAlert] = useState(false);

    const formSubmit = useCallback((values: LoginForm) => {
        setShowAuthAlert(false);
        fetcher.submit({
            username: values.username,
            password: values.password
        }, {action: "/login", method: "POST"});
    }, [fetcher]);

    useEffect(() => {
        if (fetcher.data) {
            const response = fetcher.data as { error: string };
            if (response.error) {
                setShowAuthAlert(true);
                form.resetField("password");
            }
        }

    }, [fetcher.data]);

    return (
        <div className={cn("flex flex-col gap-6", className)} {...props}>

            {showAuthAlert &&
                <Alert variant="destructive" className="max-w-md">
                    <AlertCircleIcon/>
                    <AlertTitle>Authentication failed</AlertTitle>
                    <AlertDescription>
                        Your credentials could not be processed. Please check your username and password
                        and try again.
                    </AlertDescription>
                </Alert>
            }

            <Card>
                <CardHeader>
                    <CardTitle>Login to your account</CardTitle>
                    <CardDescription>
                        Enter your username below to login to your account
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={form.handleSubmit(formSubmit)}>
                        <FieldGroup>
                            <Controller name="username" control={form.control} render={({field, fieldState}) => (
                                <Field data-invalid={fieldState.invalid}>
                                    <FieldLabel htmlFor="username">Username</FieldLabel>
                                    <Input {...field}
                                           aria-invalid={fieldState.invalid}
                                           id="username"
                                           placeholder="JohnDoe"
                                    />
                                    {fieldState.invalid && (<FieldError errors={[fieldState.error]}/>)}
                                </Field>
                            )}/>

                            <Controller name="password" control={form.control} render={({field, fieldState}) => (
                                <Field data-invalid={fieldState.invalid}>
                                    <FieldLabel htmlFor="password">Password</FieldLabel>
                                    <Input {...field}
                                           aria-invalid={fieldState.invalid}
                                           id="password"
                                           type="password"
                                    />
                                    {fieldState.invalid && (
                                        <FieldError errors={[fieldState.error]}/>
                                    )}
                                </Field>
                            )}/>

                            <Field>
                                <Button type="submit" disabled={form.formState.isSubmitting}>Login</Button>
                            </Field>
                        </FieldGroup>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}
