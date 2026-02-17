"use client"

import Link from "next/link"
import { ArrowLeft } from "lucide-react"

import { Button } from "@/components/ui/button"
import { RoleSelectionStep } from "./components/RoleSelectionStep"

export default function SignUpPage() {

    return (
        <div className="h-screen w-full grid place-items-center p-4 gradient-brand overflow-y-auto">
            <div className="w-full max-w-4xl space-y-4">
                <Button
                    variant="ghost"
                    asChild
                    className="w-fit cursor-pointer mb-0 text-white hover:text-white/80 hover:bg-white/10"
                >
                    <Link href="/login">
                        <ArrowLeft className="mr-2 h-4 w-4" />
                        Volver al login
                    </Link>
                </Button>
                <RoleSelectionStep />
            </div>
        </div>
    )
}
