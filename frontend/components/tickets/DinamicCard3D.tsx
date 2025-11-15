'use client'
import * as THREE from 'three'
import { useMemo, useEffect, useState } from 'react'

interface DynamicCard3DProps {
    code: string
    qrCode: string
    eventTitle: string
    areaName?: string
    seatNumber?: string
}

export default function DynamicCard3D({
    code,
    qrCode,
    eventTitle,
    areaName,
    seatNumber,
}: DynamicCard3DProps) {
    const [backTexture, setBackTexture] = useState<THREE.CanvasTexture | null>(null)

    // === TEXTURA FRENTE ===
    const frontTexture = useMemo(() => {
        const canvas = document.createElement('canvas')
        canvas.width = 2048
        canvas.height = 2048
        const ctx = canvas.getContext('2d')!

        // --- Fondo
        const gradient = ctx.createLinearGradient(0, 0, 2048, 2048)
        gradient.addColorStop(0, '#141414')
        gradient.addColorStop(1, '#141414')
        ctx.fillStyle = gradient
        ctx.fillRect(0, 0, 2048, 2048)

        // --- Título
        const textGradient = ctx.createLinearGradient(0, 0, canvas.width, canvas.height)
        textGradient.addColorStop(0.0, 'oklch(0.65 0.18 35)')
        textGradient.addColorStop(0.33, 'oklch(0.6 0.24 350)')
        textGradient.addColorStop(0.66, 'oklch(0.55 0.25 330)')
        textGradient.addColorStop(1.0, 'oklch(0.6 0.22 290)')
        ctx.fillStyle = textGradient
        ctx.font = 'bold 120px Poppins, sans-serif'
        ctx.fillStyle = textGradient

        drawWrappedText(
            ctx,
            eventTitle,
            200,      // x
            200,      // y inicial
            1600,     // ancho máximo permitido
            150       // lineHeight (separa líneas)
        )

        ctx.font = '75px Poppins, sans-serif'
        ctx.fillStyle = '#ccc'
        ctx.fillText(areaName || '', 200, 500)
        if (seatNumber) ctx.fillText(`Asiento: ${seatNumber}`, 180, 660)

        // === 🔥 QR (base64) ===
        const qrX = 200
        const qrY = 650
        const qrSizeY = 1000
        const qrSizeX = 1600

        const qrImg = new Image()
        qrImg.src = qrCode
        qrImg.onload = () => {
            ctx.drawImage(qrImg, qrX, qrY, qrSizeX, qrSizeY)

            tex.needsUpdate = true
        }

        // Borde del QR
        ctx.lineWidth = 6
        ctx.strokeStyle = '#fff'
        ctx.strokeRect(qrX - 4, qrY - 4, qrSizeX + 8, qrSizeY + 8)

        // Código del ticket
        ctx.fillStyle = '#ccc'
        ctx.font = 'bold 95px monospace'
        ctx.fillText(code, 650, 1900)

        const tex = new THREE.CanvasTexture(canvas)
        tex.anisotropy = 16
        tex.minFilter = THREE.LinearMipmapLinearFilter
        tex.magFilter = THREE.LinearFilter
        tex.wrapS = tex.wrapT = THREE.ClampToEdgeWrapping
        tex.needsUpdate = true

        return tex
    }, [eventTitle, areaName, seatNumber, code, qrCode])

    // === 🪪 TEXTURA DORSO (gradient-brand + logo centrado) ===
    useEffect(() => {
        const canvas = document.createElement('canvas')
        canvas.width = 2048
        canvas.height = 2048
        const ctx = canvas.getContext('2d')!

        // 🌈 Usamos tu gradiente brand exacto
        const gradient = ctx.createLinearGradient(0, 0, canvas.width, canvas.height)
        gradient.addColorStop(0, '#141414')
        gradient.addColorStop(1, '#141414')
        ctx.fillStyle = gradient
        ctx.fillRect(0, 0, canvas.width, canvas.height)

        // 🧩 Cargar el logo asincrónicamente
        const logo = new Image()
        logo.src = '/logo.png'
        logo.onload = () => {
            const size = 1600
            const x = canvas.width / 2 - size / 2
            const y = canvas.height / 2 - size / 2

            ctx.drawImage(logo, x, y, size, size)

            const tex = new THREE.CanvasTexture(canvas)
            tex.anisotropy = 16
            tex.minFilter = THREE.LinearMipmapLinearFilter
            tex.magFilter = THREE.LinearFilter
            tex.wrapS = tex.wrapT = THREE.ClampToEdgeWrapping
            tex.needsUpdate = true
            setBackTexture(tex)
        }
    }, [])

    // === 🎬 Render ===
    return (
        <mesh scale={[2.5, 3.8, 0.1]}>
            <boxGeometry args={[1, 1, 0.05]} />
            {[
                new THREE.MeshPhysicalMaterial({ color: '#111' }),
                new THREE.MeshPhysicalMaterial({ color: '#111' }),
                new THREE.MeshPhysicalMaterial({ color: '#111' }),
                new THREE.MeshPhysicalMaterial({ color: '#111' }),
                new THREE.MeshPhysicalMaterial({
                    map: frontTexture,
                    clearcoat: 1,
                    clearcoatRoughness: 0.05,
                    roughness: 0.2,
                    metalness: 0.6,
                    envMapIntensity: 1.5,
                }),
                new THREE.MeshPhysicalMaterial({
                    map: backTexture ?? null, // fallback null hasta que cargue
                    color: backTexture ? undefined : '#555',
                    clearcoat: 0.8,
                    roughness: 0.3,
                    metalness: 0.4,
                    envMapIntensity: 1.2,
                }),
            ].map((mat, i) => (
                <primitive key={i} object={mat} attach={`material-${i}`} />
            ))}
        </mesh>
    )
}

function drawWrappedText(ctx: CanvasRenderingContext2D, text: string, x: number, y: number, maxWidth: number, lineHeight: number) {
    const words = text.split(' ')
    let line = ''

    for (let i = 0; i < words.length; i++) {
        const testLine = line + words[i] + ' '
        const metrics = ctx.measureText(testLine)
        const testWidth = metrics.width

        if (testWidth > maxWidth && i > 0) {
            ctx.fillText(line, x, y)
            line = words[i] + ' '
            y += lineHeight
        } else {
            line = testLine
        }
    }

    ctx.fillText(line, x, y)
}
