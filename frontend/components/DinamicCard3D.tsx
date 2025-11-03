'use client';
import * as THREE from 'three';
import { useMemo } from 'react';

interface DynamicCard3DProps {
    eventTitle: string;
    areaName?: string;
    seatNumber?: string;
    qrCode: string;
}

export default function DynamicCard3D({
    eventTitle,
    areaName,
    seatNumber,
    qrCode,
}: DynamicCard3DProps) {
    
    const texture = useMemo(() => {
        const canvas = document.createElement('canvas');
        canvas.width = 2048;
        canvas.height = 2048;
        const ctx = canvas.getContext('2d')!;

        // === Fondo ===
        const gradient = ctx.createLinearGradient(0, 0, 2048, 2048);
        gradient.addColorStop(0, '#141414');
        gradient.addColorStop(1, '#2a2a2a');
        ctx.fillStyle = gradient;
        ctx.fillRect(0, 0, 2048, 2048);

        // === Texto principal ===
        ctx.fillStyle = '#fff';
        ctx.font = 'bold 120px Poppins, sans-serif';
        ctx.fillText(eventTitle, 160, 300);

        // === Subtexto ===
        ctx.font = '80px Poppins, sans-serif';
        ctx.fillStyle = '#ccc';
        ctx.fillText(areaName || '', 160, 500);
        if (seatNumber) ctx.fillText(`Asiento: ${seatNumber}`, 160, 640);

        // === Código alfanumérico ===
        ctx.font = '60px monospace';
        ctx.fillStyle = '#999';
        ctx.fillText(qrCode, 800, 1900);

        // === QR MOCK ===
        const qrX = 350;
        const qrY = 750;
        const qrSizeY = 900;
        const qrSizeX = 1350;

        ctx.fillStyle = '#fff';
        ctx.fillRect(qrX, qrY, qrSizeX, qrSizeY);
        ctx.fillStyle = '#000';

        // Dibujamos cuadrados pseudoaleatorios tipo QR
        for (let i = 0; i < 10; i++) {
            for (let j = 0; j < 10; j++) {
                if (Math.random() > 0.5) {
                    const x = qrX + i * (qrSizeX / 10);
                    const y = qrY + j * (qrSizeY / 10);
                    ctx.fillRect(x, y, qrSizeX / 10 - 2, qrSizeY / 10 - 2);
                }
            }
        }

        // === Marco blanco alrededor del QR ===
        ctx.lineWidth = 6;
        ctx.strokeStyle = '#fff';
        ctx.strokeRect(qrX - 4, qrY - 4, qrSizeX + 8, qrSizeY + 8);

        // === Resultado como textura ===
        const tex = new THREE.CanvasTexture(canvas);
        tex.anisotropy = 16;
        tex.minFilter = THREE.LinearMipmapLinearFilter;
        tex.magFilter = THREE.LinearFilter;
        tex.wrapS = tex.wrapT = THREE.ClampToEdgeWrapping;
        tex.needsUpdate = true;
        return tex;
    }, [eventTitle, areaName, seatNumber, qrCode]);

    
    return (
        <mesh scale={[1.7, 2.8, 0.1]}>
            <boxGeometry args={[1, 1, 0.05]} />
            <meshPhysicalMaterial
                map={texture}
                clearcoat={1}
                clearcoatRoughness={0.05}
                roughness={0.2}
                metalness={0.6}
                envMapIntensity={1.5}
            />
        </mesh>
    );
}
