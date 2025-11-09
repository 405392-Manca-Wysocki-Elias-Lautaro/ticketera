import { useState } from 'react';
import { Card, CardContent } from '../ui/card';
import StarBorder from '../StarBorder';
import { Button } from '../ui/button';
import { Calendar, Loader2, MapPin, QrCode } from 'lucide-react';
import { Badge } from '../ui/badge';

export default function TicketCard({ ticket, onViewQR }: { ticket: any; onViewQR?: () => void }) {
    const formattedDate = new Date(ticket.eventDate).toLocaleDateString("es-ES", {
        day: "numeric",
        month: "long",
        year: "numeric",
    })

    const [loading, setLoading] = useState(false);

    const handleClick = async ({ onClick }) => {
        setLoading(true);
        await new Promise((r) => setTimeout(r, 200));
        onClick();
        setTimeout(() => setLoading(false), 500);
    };

    return (
        <Card className={ticket.status === "used" ? "opacity-60" : ""}>
            <CardContent className="p-6 flex flex-col h-full">
                <div className="flex-1 space-y-4">
                    <div className="flex items-start justify-between">
                        <div className="flex-1">
                            <h3 className="font-bold text-lg line-clamp-2 mb-1">{ticket.eventTitle}</h3>
                            <Badge variant={ticket.status === "valid" ? "secondary" : "default"}>
                                {ticket.status === "valid" ? "Válido" : "Usado"}
                            </Badge>
                        </div>
                    </div>

                    <div className="space-y-2 text-sm">
                        <div className="flex items-center gap-2 text-muted-foreground">
                            <Calendar className="h-4 w-4 shrink-0" />
                            <span>
                                {formattedDate} - {ticket.eventTime}
                            </span>
                        </div>
                        <div className="flex items-center gap-2 text-muted-foreground">
                            <MapPin className="h-4 w-4 shrink-0" />
                            <span>{ticket.eventLocation}</span>
                        </div>
                    </div>

                    <div className="pt-4 border-t space-y-2">
                        <div className="flex justify-between text-sm">
                            <span className="text-muted-foreground">Área</span>
                            <span className="font-medium">{ticket.areaName}</span>
                        </div>
                        {ticket.seatNumber && (
                            <div className="flex justify-between text-sm">
                                <span className="text-muted-foreground">Asiento</span>
                                <span className="font-medium">{ticket.seatNumber}</span>
                            </div>
                        )}
                        <div className="flex justify-between text-sm">
                            <span className="text-muted-foreground">Precio</span>
                            <span className="font-medium">${ticket.price.toLocaleString()}</span>
                        </div>
                    </div>
                </div>

                {ticket.status === "valid" && onViewQR && (
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
