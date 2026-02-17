
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { CameraScannerWrapper } from "@/components/camera/CameraScannerWrapper"
import { SpinnerOverlay } from "@/components/SpinnerOverlay"
import { BarcodeFormat } from '@zxing/library'
import { Camera, Hash, QrCode, AlertCircle, CheckCircle2 } from "lucide-react"
import { Card, CardHeader, CardTitle } from "@/components/ui/card"
import GradientText from "@/components/GradientText"

interface ValidationTabsProps {
    onValidate: (type: "QR" | "CODE", value: string) => void;
    isPending: boolean;
    scanning: boolean;
    setScanning: (scanning: boolean) => void;
    ticketCode: string;
    setTicketCode: (code: string) => void;
    error: string | null;
    success: string | null;
}

export function ValidationTabs({
    onValidate,
    isPending,
    scanning,
    setScanning,
    ticketCode,
    setTicketCode,
    error,
    success
}: ValidationTabsProps) {
    return (
        <div className="flex flex-col w-full h-full">
            <Tabs defaultValue="camera" className="relative flex justify-center w-full h-full">

                {/* Tabs floating over scanner */}
                <div className="absolute top-3 left-1/2 -translate-x-1/2 z-20">
                    <TabsList className="bg-white/90 dark:bg-zinc-800/90 backdrop-blur-md shadow rounded-xl px-2 border- transparent dark:border-white/10">
                        <TabsTrigger value="camera" className="cursor-pointer w-full lg:w-50">
                            <Camera className="mr-1 h-4 w-4" /> QR
                        </TabsTrigger>
                        <TabsTrigger value="manual" className="cursor-pointer w-full lg:w-50">
                            <Hash className="mr-1 h-4 w-4" /> Manual
                        </TabsTrigger>
                    </TabsList>
                </div>

                {/* CONTENT */}
                <TabsContent value="camera" className="h-full w-full">

                    {isPending && <SpinnerOverlay />}

                    {scanning ? (
                        <CameraScannerWrapper
                            title={"Validar QR"}
                            onClose={() => setScanning(false)}
                            onDetected={(code: any) => {
                                onValidate("QR", code);
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
                    className="px-4 pt-20 space-y-4 w-full lg:w-1/2 flex flex-col justify-center mx-auto"
                >
                    {isPending && <SpinnerOverlay />}

                    <Label className="text-center">Código del Ticket</Label>
                    <Input
                        value={ticketCode}
                        onChange={(e) => setTicketCode(e.target.value)}
                        placeholder="Ingresá el código"
                    />
                    <Button
                        onClick={() => onValidate("CODE", ticketCode)}
                        className="w-full gradient-brand text-white"
                    >
                        Validar Ticket
                    </Button>

                </TabsContent>
            </Tabs>

            <div className='flex w-full justify-center mt-4 px-4 md:p-0 mb-4'>
                {error &&
                    <Card className="w-full max-w-md text-center py-2 border-destructive/50 bg-destructive/5">
                        <CardHeader className="space-y-2 px-2">
                            <div className="flex justify-center">
                                <AlertCircle className="h-8 w-8 text-destructive" />
                            </div>
                            <div>
                                <CardTitle className="text-xl text-destructive">{error}</CardTitle>
                            </div>
                        </CardHeader>
                    </Card>
                }
                {success &&
                    <Card className="w-full max-w-md text-center border-green-500/50 bg-green-500/5">
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
        </div>
    )
}
