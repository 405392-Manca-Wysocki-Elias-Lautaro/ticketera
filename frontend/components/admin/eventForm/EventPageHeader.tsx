"use client"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { ArrowLeft } from "lucide-react"
import GradientText from "@/components/GradientText"

export default function EventPageHeader({ title }: { title: string }) {
    return (
        <div className="relative flex items-center justify-center mb-8">
            <div className="absolute left-0">
                <Button variant="ghost" asChild>
                    <Link href="/admin/events">
                        <ArrowLeft className="mr-2 h-4 w-4" />
                        Volver a eventos
                    </Link>
                </Button>
            </div>

            <GradientText>
                <h1 className="text-3xl font-bold mb-8">{title}</h1>
            </GradientText>
        </div>
    )
}
