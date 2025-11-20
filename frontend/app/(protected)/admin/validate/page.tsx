"use client"

import { useEffect, useState } from "react"

import { Card, CardContent } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs"
import { Badge } from "@/components/ui/badge"
import GradientText from "@/components/GradientText"
import { RoleUtils } from "@/utils/roleUtils"
import { useAuth } from "@/hooks/auth/useAuth"
import { Button } from "@/components/ui/button"
import { Camera, Hash, QrCode } from "lucide-react"
import { mockEvents } from "@/mocks/mockEvents"
import { CameraScannerWrapper } from '@/components/camera/CameraScannerWrapper'
import { CameraScanner } from '@/components/camera/CameraScanner'
import { useRouter } from 'next/navigation'
import { BarcodeFormat } from '@zxing/library'
import { useValidateTicket } from '@/hooks/ticket/useValidateTicket'
import { toast } from 'sonner'
import { SpinnerOverlay } from '@/components/SpinnerOverlay'

export default function AdminValidatePage() {
    const router = useRouter();
    const { user, isLoading } = useAuth()

    const [selectedEventId, setSelectedEventId] = useState<string>("")
    const [ticketCode, setTicketCode] = useState("")
    const [scanning, setScanning] = useState(false);

    const assignedEvents = mockEvents.slice(0, 5)
    const selectedEvent = assignedEvents.find((e) => e.id === selectedEventId)

    // redirect if not admin
    useEffect(() => {
        if (!isLoading && (!user || !RoleUtils.isAdmin(user))) {
            router.push("/app/dashboard")
        }
    }, [user, isLoading, router]);

    const { mutate: validate, isPending } = useValidateTicket();

    function handleValidate(type: "QR" | "CODE", value: string) {
        if (!value || isPending) return;

        validate(
            { type, value },
            {
                onSuccess: (res: any) => {
                    toast.success(`Ticket ${res.data?.code} validado correctamente`);
                },
                onError: (err: any) => {
                    toast.error(err?.response?.data?.message ?? "Error validando ticket");
                },
            }
        );
    }


    if (isLoading || !user) {
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
            <Card className="mx-4 mb-2">
                <CardContent className="py-4 space-y-3">

                    {/* Select */}
                    <Label className="font-medium">Seleccionar Evento</Label>
                    <select
                        className="border rounded-md p-2 w-full"
                        value={selectedEventId}
                        onChange={(e) => setSelectedEventId(e.target.value)}
                    >
                        <option value="">Selecciona un evento</option>
                        {assignedEvents.map((ev) => (
                            <option key={ev.id} value={ev.id}>
                                {ev.title} – {new Date(ev.date).toLocaleDateString("es-ES")}
                            </option>
                        ))}
                    </select>

                    {/* Minimal info */}
                    {selectedEvent && (
                        <div className="flex gap-2 text-sm text-muted-foreground flex-wrap">
                            <Badge variant="outline">{selectedEvent.location}</Badge>
                            <Badge variant="outline">{new Date(selectedEvent.date).toLocaleDateString()}</Badge>
                            <Badge variant="outline">{selectedEvent.availableTickets} tickets</Badge>
                        </div>
                    )}
                </CardContent>
            </Card>

            {/* VALIDATION + SCANNER */}
            {selectedEvent && (
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

                        <Label>Código del Ticket</Label>
                        <Input
                            value={ticketCode}
                            onChange={(e) => setTicketCode(e.target.value)}
                            placeholder="Ingresá el código"
                        />
                        <Button 
                            onClick={ () => handleValidate("QR", ticketCode)}
                            className="w-full gradient-brand text-white"
                        >
                            Validar Ticket
                        </Button>
                    </TabsContent>
                </Tabs>
            )}
        </div>
    )
}
