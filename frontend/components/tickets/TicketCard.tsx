import { useState } from 'react';
import { Card, CardContent } from '../ui/card';
import StarBorder from '../StarBorder';
import { Button } from '../ui/button';
import { Calendar, Loader2, MapPin, QrCode, Ticket as TicketIcon } from 'lucide-react';
import { Badge } from '../ui/badge';
import { TicketResponse, TicketStatus } from '@/types/Ticket';

export default function TicketCard({ ticket, onViewQR }: { ticket: TicketResponse; onViewQR?: () => void }) {
    const formattedDate = ticket.issuedAt ? new Date(ticket.issuedAt).toLocaleDateString("es-ES", {
        day: "numeric",
        month: "long",
        year: "numeric",
    }) : "Fecha no disponible";
    
    const formattedTime = ticket.issuedAt ? new Date(ticket.issuedAt).toLocaleTimeString("es-ES", {
        hour: "2-digit",
        minute: "2-digit",
    }) : "";

    const [loading, setLoading] = useState(false);

    const handleClick = async ({ onClick }: { onClick: () => void }) => {
        setLoading(true);
        await new Promise((r) => setTimeout(r, 200));
        onClick();
        setTimeout(() => setLoading(false), 500);
    };

    const isValid = ticket.status === TicketStatus.ISSUED;
    const isUsed = ticket.status === TicketStatus.CHECKED_IN;

    return (
        <Card className={!isValid ? "opacity-60" : ""}>
            <CardContent className="p-6 flex flex-col h-full">
                <div className="flex-1 space-y-4">
                    <div className="flex items-start justify-between">
                        <div className="flex-1">
                            <h3 className="font-bold text-xl mb-2 line-clamp-2">
                                {ticket.eventTitle || "Tu Entrada"}
                            </h3>
                            <Badge variant={isValid ? "secondary" : "default"} className="text-sm">
                                {isValid ? "✓ Válido" : isUsed ? "✓ Usado" : ticket.status}
                            </Badge>
                        </div>
                        <TicketIcon className="h-8 w-8 text-primary opacity-50" />
                    </div>

                    <div className="space-y-3 text-sm">
                        <div className="bg-muted/50 p-3 rounded-lg">
                            <div className="flex items-center gap-2 text-foreground font-medium mb-1">
                                <Calendar className="h-4 w-4 shrink-0" />
                                <span>Fecha de compra</span>
                            </div>
                            <p className="text-muted-foreground ml-6">
                                {formattedDate} • {formattedTime}
                            </p>
                        </div>
                        
                        {isUsed && ticket.checkedInAt && (
                            <div className="bg-green-500/10 p-3 rounded-lg border border-green-500/20">
                                <div className="flex items-center gap-2 text-green-700 dark:text-green-400 font-medium mb-1">
                                    <Calendar className="h-4 w-4 shrink-0" />
                                    <span>Ingreso registrado</span>
                                </div>
                                <p className="text-green-600 dark:text-green-500 ml-6 text-xs">
                                    {new Date(ticket.checkedInAt).toLocaleDateString("es-ES", {
                                        day: "numeric",
                                        month: "long",
                                        year: "numeric",
                                        hour: "2-digit",
                                        minute: "2-digit"
                                    })}
                                </p>
                            </div>
                        )}
                    </div>

                    <div className="pt-4 border-t">
                        <div className="flex justify-between items-center text-xs text-muted-foreground">
                            <span>Código de verificación</span>
                            <span className="font-mono font-medium">{ticket.code}</span>
                        </div>
                    </div>
                </div>

                {isValid && onViewQR && (
                    <div className="mt-4 w-full">
                        <StarBorder className='w-full'>
                            <Button
                                onClick={() => handleClick({ onClick: onViewQR })}
                                className="w-full gradient-brand text-white"
                                disabled={loading}
                            >
                                {loading ? (
                                    <>
                                        <Loader2 className="animate-spin w-4 h-4" />
                                    </>
                                ) : (
                                    <>
                                        <QrCode className="mr-2 h-4 w-4" />
                                        Ver Código QR
                                    </>
                                )}
                            </Button>
                        </StarBorder>
                    </div>
                )}
            </CardContent>
        </Card>
    )
}
