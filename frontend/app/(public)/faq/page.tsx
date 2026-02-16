"use client"

import {
    Accordion,
    AccordionContent,
    AccordionItem,
    AccordionTrigger,
} from "@/components/ui/accordion"
import { Navbar } from "@/components/Navbar"
import GradientText from "@/components/GradientText"
import { Card, CardContent } from "@/components/ui/card"
import { Mail, MessageCircle, Info } from "lucide-react"

export default function FAQPage() {
    const faqs = [
        {
            id: "item-1",
            question: "¿Cómo creo mi clave?",
            answer: "Para crear tu clave, debes iniciar el proceso de registro en nuestra plataforma. Se te pedirá que defiendas una contraseña segura que cumpla con nuestros requisitos de seguridad (mínimo 8 caracteres, mayúsculas, números y caracteres especiales)."
        },
        {
            id: "item-2",
            question: "¿Cómo desbloqueo mi usuario?",
            answer: "Si has bloqueado tu usuario por múltiples intentos fallidos, debes utilizar la opción 'Olvidé mi contraseña' en la pantalla de login. Recibirás un correo con las instrucciones para restablecer tu acceso. Si el problema persiste, contacta a soporte."
        },
        {
            id: "item-3",
            question: "¿A qué dirección de mail me puedo contactar para hacer una sugerencia o reclamo?",
            answer: "Puedes escribirnos a contacto@ticketly.com para cualquier sugerencia, reclamo o consulta general. Nuestro equipo te responderá a la brevedad."
        },
        {
            id: "item-4",
            question: "¿Cómo funciona Ticketly?",
            answer: "Ticketly es una plataforma que facilita la compra y venta de entradas para eventos. Puedes buscar eventos, seleccionar tus asientos y pagar de forma segura. Recibirás tus tickets digitales directamente en tu correo y en la app."
        },
        {
            id: "item-5",
            question: "¿Qué necesito para comprar?",
            answer: "Solo necesitas registrarte en nuestra plataforma con un correo electrónico válido, seleccionar el evento de tu interés y contar con un medio de pago habilitado."
        },
        {
            id: "item-6",
            question: "¿Qué medios de pago aceptan?",
            answer: "Aceptamos todas las principales tarjetas de crédito y débito (Visa, Mastercard), así como pagos a través de Mercado Pago."
        }
    ]

    return (
        <div className="h-screen bg-background flex flex-col overflow-y-auto">
            <Navbar />
            <div className="flex-1 w-full max-w-4xl mx-auto px-6 py-12">
                {/* Header */}
                <div className="mb-12 text-center space-y-4">
                    <GradientText>
                        <h1 className="text-4xl md:text-5xl font-bold">Preguntas Frecuentes</h1>
                    </GradientText>
                    <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
                        Encuentra respuestas rápidas a las dudas más comunes sobre el uso de Ticketly y nuestros servicios.
                    </p>
                </div>

                <div className="grid gap-8 md:grid-cols-[2fr_1fr]">
                    {/* FAQ List */}
                    <div className="space-y-6">
                        <Card className="border-none shadow-none bg-transparent">
                            <CardContent className="p-0">
                                <Accordion type="single" collapsible className="w-full space-y-4">
                                    {faqs.map((faq) => (
                                        <AccordionItem key={faq.id} value={faq.id} className="border px-4 py-1 rounded-lg bg-card shadow-sm">
                                            <AccordionTrigger className="text-left font-medium hover:no-underline">
                                                <div className="flex items-center gap-3">
                                                    <Info className="h-5 w-5 text-primary shrink-0" />
                                                    {faq.question}
                                                </div>
                                            </AccordionTrigger>
                                            <AccordionContent className="text-muted-foreground pt-2 pl-8">
                                                {faq.answer}
                                            </AccordionContent>
                                        </AccordionItem>
                                    ))}
                                </Accordion>
                            </CardContent>
                        </Card>
                    </div>

                    {/* Contact Section */}
                    <div className="space-y-6">
                        <div className="sticky top-24 space-y-6">
                            <Card>
                                <CardContent className="pt-6 space-y-4">
                                    <h3 className="font-semibold text-xl mb-4">¿Tienes alguna otra duda?</h3>
                                    <p className="text-sm text-muted-foreground mb-6">
                                        Si no encuentras la respuesta que buscas, no dudes en contactarnos directamente.
                                    </p>

                                    <div className="space-y-4">
                                        <div className="flex items-center gap-3 text-sm">
                                            <div className="h-10 w-10 rounded-full bg-primary/10 flex items-center justify-center text-primary">
                                                <Mail className="h-5 w-5" />
                                            </div>
                                            <div>
                                                <p className="font-medium">Email</p>
                                                <a href="mailto:contacto@ticketly.com" className="text-muted-foreground hover:text-primary transition-colors">contacto@ticketly.com</a>
                                            </div>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
