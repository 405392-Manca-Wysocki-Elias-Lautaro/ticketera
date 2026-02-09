
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Button } from "@/components/ui/button"

export function TermsModal({ children }: { children?: React.ReactNode }) {
    return (
        <Dialog>
            <DialogTrigger asChild>
                {children || <Button variant="link" className="p-0 h-auto font-normal text-primary underline-offset-4 hover:underline">Términos y Condiciones</Button>}
            </DialogTrigger>
            <DialogContent className="max-w-3xl h-[80vh] flex flex-col p-6 gap-0">
                <DialogHeader className="pb-4 shrink-0">
                    <DialogTitle className="text-2xl font-bold">Términos y Condiciones</DialogTitle>
                    <DialogDescription>
                        Por favor lee atentamente los términos de servicio de Ticketly.
                    </DialogDescription>
                </DialogHeader>
                <ScrollArea className="flex-1 pr-4 -mr-4">
                    <div className="space-y-6 text-sm text-foreground/90 pr-4">
                        <section className="space-y-2">
                            <h3 className="font-semibold text-lg">Generalidades sobre los Términos de Servicio</h3>
                            <p>
                                Bienvenido a Ticketly. Nos complace ofrecerle acceso al Servicio (como se define más abajo),
                                sujeto a estos términos y condiciones y a la Política de Privacidad
                                correspondiente de Ticketly Al acceder y utilizar el Servicio, usted expresa su consentimiento,
                                acuerdo y entendimiento de los Términos de Servicio y la Política de Privacidad. Si no está de
                                acuerdo con los Términos de Servicio o la Política de Privacidad, no utilice el Servicio.
                            </p>
                            <p>
                                Si utiliza el servicio está aceptando las modalidades operativas en vigencia descriptas más
                                adelante, las declara conocer y aceptar, las que se habiliten en el futuro y en los términos y
                                condiciones que a continuación se detallan:
                            </p>
                        </section>

                        <section className="space-y-2">
                            <h3 className="font-semibold text-lg">Operaciones habilitadas</h3>
                            <p>
                                Las operaciones habilitadas son aquellas que estarán disponibles para los clientes, quienes
                                deberán cumplir los requisitos que se encuentren vigentes en su momento para operar el
                                Servicio. Las mismas podrán ser ampliadas o restringidas por el proveedor, comunicándolo
                                previamente con una antelación no menor a 60 días, y comprenden entre otras, sin que pueda
                                entenderse taxativamente las que se indican a continuación:
                            </p>
                        </section>

                        <section className="space-y-2">
                            <h3 className="font-semibold text-lg">Transacciones</h3>
                            <p>
                                En ningún caso debe entenderse que la solicitud de un producto o servicio implica obligación
                                alguna para el Acceso y uso del Servicio.
                            </p>
                            <p>
                                Para operar el Servicio se requerirá siempre que se trate de clientes de Ticketly, quienes
                                podrán acceder mediante cualquier dispositivo con conexión a la Red Internet. El cliente deberá
                                proporcionar el número de documento de identidad y la clave personal, que será provista por la
                                aplicación como requisito previo a la primera operación, en la forma que le sea requerida. La
                                clave personal y todo o cualquier otro mecanismo adicional de autenticación personal provisto
                                por el Banco tiene el carácter de secreto e intransferible, y por lo tanto asumo las consecuencias
                                de su divulgación a terceros, liberando a Ticketly de toda responsabilidad que
                                de ello se derive. En ningún caso Ticketly requerirá que le suministre la totalidad de los
                                datos, ni enviará mail requiriendo información personal alguna.
                            </p>
                        </section>

                        <section className="space-y-2">
                            <h3 className="font-semibold text-lg">Costo del Servicio</h3>
                            <p>
                                La empresa Ticketly podrá cobrar comisiones por el mantenimiento y/o uso de este Servicio o los
                                que en el futuro implemente, entendiéndose facultado expresamente para efectuar los
                                correspondientes débitos en mis cuentas, aún en descubierto, por lo que presto para ello mi
                                expresa conformidad. En caso de cualquier modificación a la presente previsión, lo comunicará
                                con al menos 60 días de antelación.
                            </p>
                        </section>

                        <section className="space-y-2">
                            <h3 className="font-semibold text-lg">Vigencia</h3>
                            <p>
                                El Usuario podrá dejar sin efecto la relación que surja de la presente, en forma inmediata, sin
                                otra responsabilidad que la derivada de los gastos originados hasta ese momento. Si el cliente
                                incumpliera cualquiera de las obligaciones asumidas en su relación contractual con empresa
                                Ticketly, o de los presentes Términos y Condiciones, Ticketly podrá decretar la caducidad del
                                presente Servicio en forma inmediata, sin que ello genere derecho a indemnización o
                                compensación alguna. Ticketly podrá dejar sin efecto la relación que surja de la presente,
                                con un preaviso mínimo de 60 días, sin otra responsabilidad.
                            </p>
                        </section>

                        <section className="space-y-2">
                            <h3 className="font-semibold text-lg">Validez de operaciones y notificaciones</h3>
                            <p>
                                Los registros emitidos por la app serán prueba suficiente de las operaciones cursadas por dicho
                                canal. Renuncio expresamente a cuestionar la idoneidad o habilidad de ese medio de prueba.
                                A los efectos del cumplimiento de disposiciones legales o contractuales, se otorga a las
                                notificaciones por este medio el mismo alcance de las notificaciones mediante documento
                                escrito.
                            </p>
                        </section>

                        <section className="space-y-2">
                            <h3 className="font-semibold text-lg">Propiedad intelectual</h3>
                            <p>
                                El software en Argentina está protegido por la ley 11.723, que regula la propiedad intelectual y
                                los derechos de autor de todos aquellos creadores de obras artísticas, literarias y científicas.
                            </p>
                        </section>

                        <section className="space-y-2">
                            <h3 className="font-semibold text-lg">Privacidad de la información</h3>
                            <p>
                                Para utilizar los Servicios ofrecidos por Ticketly, los usuarios deberán facilitar determinados
                                datos de carácter personal. Su información personal se procesa y almacena en servidores o
                                medios magnéticos que mantienen altos estándares de seguridad y protección tanto física
                                como tecnológica. Para mayor información sobre la privacidad de los Datos Personales y casos
                                en los que será revelada la información personal, se pueden consultar nuestras políticas de
                                privacidad.
                            </p>
                        </section>
                    </div>
                </ScrollArea>
                <div className="pt-4 flex justify-end shrink-0">
                    <DialogTrigger asChild>
                        <Button type="button" variant="secondary">Cerrar</Button>
                    </DialogTrigger>
                </div>
            </DialogContent>
        </Dialog>
    )
}
