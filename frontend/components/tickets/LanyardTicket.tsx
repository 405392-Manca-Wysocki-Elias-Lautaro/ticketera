"use client";

import { Canvas, invalidate } from "@react-three/fiber";
import { Environment, Lightformer } from "@react-three/drei";
import { Physics } from "@react-three/rapier";
import * as THREE from "three";
import Lanyard from "@/components/tickets/Lanyard";
import { motion } from "framer-motion";
import { Suspense, useEffect } from 'react';

interface LanyardTicketProps {
    code: string;
    qrCode: string;
    eventTitle: string;
    areaName?: string;
    seatNumber?: string;
    onClose?: () => void;
}

export default function LanyardTicket({
    code,
    qrCode,
    eventTitle,
    areaName,
    seatNumber,
    onClose,
}: LanyardTicketProps) {

    useEffect(() => {
        const timeout = setTimeout(() => {
            window.dispatchEvent(new Event('resize'));
            invalidate();
        }, 100);

        return () => clearTimeout(timeout);
    }, []);


    useEffect(() => {
        document.body.style.overflow = 'hidden';
        return () => {
            document.body.style.overflow = 'auto';
        };
    }, []);

    return (
        <div
            className="fixed inset-0 bg-black/60 flex items-start justify-center pt-10 z-50"
            onClick={onClose}
        >
            <div
                className="relative w-screen h-screen cursor-grab active:cursor-grabbing flex justify-center"
                onClick={(e) => e.stopPropagation()}
            >
                <motion.div
                    initial={{ y: -200, opacity: 0 }}
                    animate={{ y: 0, opacity: 1 }}
                    exit={{ y: -200, opacity: 0 }}
                    transition={{ type: "spring", stiffness: 100, damping: 20 }}
                    className="relative w-screen h-screen max-w-2xl cursor-grab active:cursor-grabbing"
                    onClick={(e) => e.stopPropagation()}
                >

                    <div className="absolute inset-0 z-50">
                        <Suspense
                            fallback={
                                <div className="flex items-center justify-center h-full">
                                    <div className="text-white">Cargando QR...</div>
                                </div>
                            }
                        >
                            <Canvas
                                className="fixed! inset-0! w-screen h-screen"
                                camera={{ position: [0, 0, 9], fov: window.innerWidth < 768 ? 50 : 35 }}
                                gl={{ alpha: true }}
                                onCreated={({ gl }) => gl.setClearColor(new THREE.Color(0x000000), 0)}
                            >
                                <ambientLight intensity={Math.PI} />
                                <Physics gravity={[0, -25, 0]} timeStep="vary">
                                    <Lanyard
                                        code={code}
                                        qrCode={qrCode}
                                        eventTitle={eventTitle}
                                        areaName={areaName}
                                        seatNumber={seatNumber}
                                    />
                                </Physics>

                                <Environment blur={0.75}>
                                    <Lightformer
                                        intensity={2}
                                        color="white"
                                        position={[0, -1, 5]}
                                        rotation={[0, 0, Math.PI / 3]}
                                        scale={[100, 0.1, 1]}
                                    />
                                </Environment>
                            </Canvas>
                        </Suspense>
                    </div>
                </motion.div>

                {/* Overlay textual con el QR */}
                {/* <div className="absolute bottom-10 left-1/2 -translate-x-1/2 text-center text-white pointer-events-none">
                    <h3 className="font-bold text-lg">{eventTitle}</h3>
                    <p className="text-sm opacity-80">
                        {areaName}
                        {seatNumber ? ` - Asiento ${seatNumber}` : ""}
                    </p>
                    <p className="font-mono text-xs mt-1">{qrCode}</p>
                </div> */}

                {/* Botón para cerrar */}
                <button
                    onClick={onClose}
                    className="
                    absolute top-5 right-5 bg-white/20 z-60
                    hover:bg-white/40 text-white font-medium 
                    px-3 py-1.5 rounded-md backdrop-blur-sm transition"
                >
                    ✕ Cerrar
                </button>
            </div>
        </div>
    );
}
