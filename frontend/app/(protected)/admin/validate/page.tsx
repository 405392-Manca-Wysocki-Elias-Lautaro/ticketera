"use client"

import { useEffect, useState } from "react"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs"
import { Badge } from "@/components/ui/badge"
import GradientText from "@/components/GradientText"
import { RoleUtils } from "@/utils/roleUtils"
import { useAuth } from "@/hooks/auth/useAuth"
import { Button } from "@/components/ui/button"
import { AlertCircle, Camera, CheckCircle2, Hash, QrCode } from "lucide-react"
import { CameraScannerWrapper } from '@/components/camera/CameraScannerWrapper'
import { useRouter } from 'next/navigation'
import { BarcodeFormat } from '@zxing/library'
import { useValidateTicket } from '@/hooks/ticket/useValidateTicket'
import { SpinnerOverlay } from '@/components/SpinnerOverlay'
import { useEvents } from '@/hooks/event/useEvents'
import { Event } from '@/types/Event'
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"

export default function AdminValidatePage() {
    const router = useRouter();
    const { user, isLoading } = useAuth()

    const [selectedEventId, setSelectedEventId] = useState<string>("")
    const [ticketCode, setTicketCode] = useState("")
    const [scanning, setScanning] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const { data: assignedEvents, isLoading: isLoadingEvents } = useEvents();

    const selectedEvent = assignedEvents?.find((e: Event) => e.id === selectedEventId)

    // redirect if not admin
    useEffect(() => {
        if (!isLoading && (!user || !RoleUtils.isAdmin(user))) {
            router.push("/dashboard")
        }
    }, [user, isLoading, router]);

    const { mutate: validate, isPending } = useValidateTicket();

    function handleValidate(type: "QR" | "CODE", value: string) {

        setError(null);
        setSuccess(null);

        if (!value || isPending) return;

        validate(
            { type, value },
            {
                onSuccess: (res: any) => {
                    setSuccess(res.data?.data?.code);
                },
                onError: (err: any) => {

                    const code = err?.response?.data?.data?.code;

                    switch (code) {
                        case "TICKET_NOT_FOUND":
                            setError("El ticket no existe o ha sido eliminado.");
                            break;

                        case "TICKET_ALREADY_CHECKED_IN":
                            setError("Este ticket ya fue registrado previamente.");
                            break;

                        case "INVALID_TICKET_STATUS":
                            setError("El estado del ticket no permite el ingreso.");
                            break;

                        case "INVALID_QR_TOKEN":
                            setError("El código QR es inválido o ha sido manipulado.");
                            break;

                        case "EXPIRED_TICKET":
                            setError("El ticket ha expirado y no puede ser utilizado.");
                            break;

                        case "INVALID_TICKET_VALIDATION_TYPE":
                            setError("Este ticket no puede validarse con el método actual.");
                            break;

                        default:
                            setError("Error al validar el ticket.");
                            break;
                    }

                    console.error(err?.response?.data?.message)
                },
            }
        );
    }

    if (isLoading || !user || isLoadingEvents || !assignedEvents) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <div className="flex flex-col w-full h-screen overflow-hidden">

            {/* TITLE */}
            <div className="text-center py-3">
                <GradientText>
                    <h1 className="text-2xl font-bold">Validación de Tickets</h1>
                </GradientText>
            </div>

            {/* SMALL EVENT SELECT / INFO */}
            <Card className="mx-4 mb-2 py-1">
                <CardContent className="py-4 space-y-3">

                    {/* Select */}
                    <Label className="font-medium">Seleccionar Evento</Label>
                    <Select value={selectedEventId} onValueChange={setSelectedEventId}>
                        <SelectTrigger className="w-full">
                            <SelectValue placeholder="Selecciona un evento" />
                        </SelectTrigger>
                        <SelectContent>
                            {assignedEvents.map((ev) => (
                                <SelectItem key={ev.id} value={ev.id}>
                                    {ev.title} – {new Date(ev.date).toLocaleDateString("es-ES")}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>

                    {/* Minimal info */}
                    {selectedEvent && (
                        <div className="flex gap-2 text-sm text-muted-foreground flex-wrap">
                            <Badge variant="outline">{selectedEvent.venueName}</Badge>
                            <Badge variant="outline">{new Date(selectedEvent.startsAt).toLocaleDateString()}</Badge>
                            <Badge variant="outline">{selectedEvent.totalAvailableTickets} tickets</Badge>
                        </div>
                    )}
                </CardContent>
            </Card>

            {/* VALIDATION + SCANNER */}
            {selectedEvent && (
                <>
                    <Tabs defaultValue="camera" className="relative flex justify-center w-full h-full">

                        {/* Tabs floating over scanner */}
                        <div className="absolute top-3 left-1/2 -translate-x-1/2 z-20">
                            <TabsList className="bg-white/90 backdrop-blur-md shadow rounded-xl px-2">
                                <TabsTrigger value="camera" className="cursor-pointer w-full lg:w-50">
                                    <Camera className="mr-1 h-4 w-4" /> QR
                                </TabsTrigger>
                                <TabsTrigger value="manual" className="cursor-pointer w-full lg:w-50">
                                    <Hash className="mr-1 h-4 w-4" /> Manual
                                </TabsTrigger>
                            </TabsList>
                        </div>

                        {/* CONTENT */}
                        <TabsContent value="camera" className="h-full">

                            {isPending && <SpinnerOverlay />}

                            {scanning ? (
                                <CameraScannerWrapper
                                    title={"Validar QR"}
                                    onClose={() => setScanning(false)}
                                    onDetected={(code: any) => {
                                        handleValidate("QR", code);
                                        setScanning(false);
                                    }}
                                    formats={[BarcodeFormat.QR_CODE]}
                                />
                            ) : (
                                <div className="flex h-full items-center justify-center">
                                    <Button
                                        className="gradient-brand"
                                        onClick={() => setScanning(true)}
                                    >
                                        <QrCode className="mr-2" /> Validar QR
                                    </Button>
                                </div>
                            )}
                        </TabsContent>

                        <TabsContent
                            value="manual"
                            className="px-4 pt-20 space-y-4 w-full lg:w-1/2 flex flex-col justify-center"
                        >
                            {isPending && <SpinnerOverlay />}

                            <Label className="text-center">Código del Ticket</Label>
                            <Input
                                value={ticketCode}
                                onChange={(e) => setTicketCode(e.target.value)}
                                placeholder="Ingresá el código"
                            />
                            <Button
                                onClick={() => handleValidate("CODE", ticketCode)}
                                className="w-full gradient-brand text-white"
                            >
                                Validar Ticket
                            </Button>

                        </TabsContent>
                    </Tabs>
                    <div className='flex w-full justify-center mt-4 px-4 md:p-0'>
                        {error &&
                            <Card className="w-full max-w-md text-center py-2">
                                <CardHeader className="space-y-2 px-2">
                                    <div className="flex justify-center">
                                        <AlertCircle className="h-8 w-8 text-destructive" />
                                    </div>
                                    <div>
                                        <GradientText>
                                            <CardTitle className="text-xl">{error}</CardTitle>
                                        </GradientText>
                                    </div>
                                </CardHeader>
                            </Card>
                        }
                        {success &&
                            <Card className="w-full max-w-md text-center">
                                <CardHeader className="space-y-4">
                                    <div className="flex justify-center">
                                        <CheckCircle2 className="h-12 w-12 text-green-500" />
                                    </div>
                                    <div>
                                        <GradientText>
                                            <CardTitle className="text-2xl">Ticket {success} validado exitosamente</CardTitle>
                                        </GradientText>
                                    </div>
                                </CardHeader>
                            </Card>
                        }
                    </div>
                </>
            )}
        </div>
    )
}
