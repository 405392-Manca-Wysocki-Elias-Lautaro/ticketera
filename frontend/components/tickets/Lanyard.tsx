'use client';
import { useEffect, useRef, useState } from 'react';
import { extend, useFrame } from '@react-three/fiber';
import { useTexture, Environment, Lightformer } from '@react-three/drei';
import {
    BallCollider,
    CuboidCollider,
    Physics,
    RigidBody,
    useRopeJoint,
    useSphericalJoint,
    RigidBodyProps
} from '@react-three/rapier';
import { MeshLineGeometry, MeshLineMaterial } from 'meshline';
import * as THREE from 'three';
import DynamicCard3D from './DinamicCard3D';
import { useDeviceContext } from '@/hooks/useDeviceContext';


extend({ MeshLineGeometry, MeshLineMaterial });

export default function Lanyard({
    code,
    qrCode,
    eventTitle,
    areaName,
    seatNumber,
    ...props
}: {
    code: string;
    qrCode: string;
    eventTitle: string;
    areaName?: string;
    seatNumber?: string;
    scale?: number | [number, number, number];
    position?: [number, number, number];
    rotation?: [number, number, number];
}) {

    return (
        <group {...props}>
            <ambientLight intensity={Math.PI} />
            <Physics gravity={[0, -25, 0]} timeStep={1 / 60}>
                <Band
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
                <Lightformer
                    intensity={3}
                    color="white"
                    position={[-1, -1, 1]}
                    rotation={[0, 0, Math.PI / 3]}
                    scale={[100, 0.1, 1]}
                />
                <Lightformer
                    intensity={3}
                    color="white"
                    position={[1, 1, 1]}
                    rotation={[0, 0, Math.PI / 3]}
                    scale={[100, 0.1, 1]}
                />
                <Lightformer
                    intensity={10}
                    color="white"
                    position={[-10, 0, 14]}
                    rotation={[0, Math.PI / 2, Math.PI / 3]}
                    scale={[100, 10, 1]}
                />
            </Environment>
        </group>

    );
}

interface BandProps {
    code: string;
    qrCode: string;
    eventTitle: string;
    areaName?: string;
    seatNumber?: string;
    maxSpeed?: number;
    minSpeed?: number;
}

function Band({
    code,
    qrCode,
    eventTitle,
    areaName,
    seatNumber,
    maxSpeed = 30,
    minSpeed = 2
}: BandProps) {
    const band = useRef<any>(null);
    const fixed = useRef<any>(null);
    const j1 = useRef<any>(null);
    const j2 = useRef<any>(null);
    const j3 = useRef<any>(null);
    const card = useRef<any>(null);

    const isMobile = useDeviceContext();

    const vec = new THREE.Vector3();
    const ang = new THREE.Vector3();
    const rot = new THREE.Vector3();
    const dir = new THREE.Vector3();

    const segmentProps: any = {
        type: 'dynamic' as RigidBodyProps['type'],
        canSleep: true,
        colliders: false,
        angularDamping: 12,
        linearDamping: 8,
    };

    const texture = useTexture('/textures/lanyard.png');
    texture.anisotropy = 16;
    texture.minFilter = THREE.LinearMipmapLinearFilter;
    texture.magFilter = THREE.LinearFilter;
    texture.wrapS = texture.wrapT = THREE.RepeatWrapping;

    const [curve] = useState(
        () =>
            new THREE.CatmullRomCurve3([
                new THREE.Vector3(),
                new THREE.Vector3(),
                new THREE.Vector3(),
                new THREE.Vector3(),
            ])
    );

    const [dragged, drag] = useState<false | THREE.Vector3>(false);
    const [hovered, hover] = useState(false);
    const [scale, setScale] = useState(1);
    const [cordLength, setCordLength] = useState(1);

    // 🔹 Responsividad
    useEffect(() => {
        const handleResize = () => {
            const w = window.innerWidth;

            if (w < 600) {
                setScale(1.15);      // antes 1.35 (demasiado)
                setCordLength(0.78);
            } else if (w < 1024) {
                setScale(1.05);      // antes 1.1
                setCordLength(0.82);
            } else {
                setScale(0.95);      // antes 1.08
                setCordLength(1);
            }
        };

        handleResize();
        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    useRopeJoint(fixed, j1, [[0, 0, 0], [0, 0, 0], cordLength]);
    useRopeJoint(j1, j2, [[0, 0, 0], [0, 0, 0], cordLength]);
    useRopeJoint(j2, j3, [[0, 0, 0], [0, 0, 0], cordLength]);
    useSphericalJoint(j3, card, [[0, 0, 0], [0, 0.85, 0]]);

    // Cursor de agarre
    useEffect(() => {
        if (hovered) {
            document.body.style.cursor = dragged ? 'grabbing' : 'grab';
            return () => {
                document.body.style.cursor = 'auto';
            };
        }
    }, [hovered, dragged]);

    useFrame((state, delta) => {
        if (dragged && typeof dragged !== 'boolean') {
            vec.set(state.pointer.x, state.pointer.y, 0.5).unproject(state.camera);
            dir.copy(vec).sub(state.camera.position).normalize();
            vec.add(dir.multiplyScalar(state.camera.position.length()));
            const next = new THREE.Vector3(
                vec.x - dragged.x,
                vec.y - dragged.y,
                vec.z - dragged.z
            );
            const current = card.current.translation();
            card.current.setNextKinematicTranslation({
                x: THREE.MathUtils.lerp(current.x, next.x, 0.15),
                y: THREE.MathUtils.lerp(current.y, next.y, 0.15),
                z: THREE.MathUtils.lerp(current.z, next.z, 0.15),
            });
        }

        if (fixed.current) {
            [j1, j2].forEach((ref) => {
                if (!ref.current.lerped)
                    ref.current.lerped = new THREE.Vector3().copy(ref.current.translation());
                ref.current.lerped.lerp(
                    ref.current.translation(),
                    delta * THREE.MathUtils.lerp(minSpeed, maxSpeed, 0.1)
                );
            });

            curve.points[0].copy(j3.current.translation());
            curve.points[1].copy(j2.current.lerped);
            curve.points[2].copy(j1.current.lerped);
            curve.points[3].copy(fixed.current.translation());
            band.current.geometry.setPoints(curve.getPoints(32));

            ang.copy(card.current.angvel());
            rot.copy(card.current.rotation());
            card.current.setAngvel({ x: ang.x, y: ang.y - rot.y * 0.25, z: ang.z });
        }
    });

    const [cardPosition, setCardPosition] = useState<[number, number, number]>([0, 3.9, 0]);

    useEffect(() => {
        const updateScale = () => {
            const w = window.innerWidth;

            if (w < 600) setCardPosition([0, 3.9, 0]);
            else setCardPosition([0, 3.9, 0]);
        };

        updateScale();
        window.addEventListener("resize", updateScale);
        return () => window.removeEventListener("resize", updateScale);
    }, []);

    const [anchorPosition, setAnchorPosition] = useState<[number, number, number]>([0, 5.5, 0]);

    useEffect(() => {
        const updateAnchor = () => {
            const w = window.innerWidth;

            if (w < 600) {
                setAnchorPosition([0, 0.8, 0]);
            } else {
                setAnchorPosition([0, -0.2, 0]);
            }
        };

        updateAnchor();
        window.addEventListener("resize", updateAnchor);
        return () => window.removeEventListener("resize", updateAnchor);
    }, []);

    const [lineWidth, setLineWidth] = useState<number>(0.3);

    useEffect(() => {
        const updateLineWidth = () => {
            const w = window.innerWidth;

            if (w < 600) {
                setLineWidth(1.5);
            } else {
                setLineWidth(0.5);
            }
        };

        updateLineWidth();
        window.addEventListener("resize", updateLineWidth);
        return () => window.removeEventListener("resize", updateLineWidth);
    }, []);

    return (
        <>
            <group position={cardPosition} scale={scale}>
                <RigidBody ref={fixed} {...segmentProps} type="fixed" position={anchorPosition} />

                <RigidBody position={[0.5, 0, 0]} ref={j1} {...segmentProps}>
                    <BallCollider args={[0.1]} />
                </RigidBody>
                <RigidBody position={[1, 0, 0]} ref={j2} {...segmentProps}>
                    <BallCollider args={[0.1]} />
                </RigidBody>
                <RigidBody position={[1.5, 0, 0]} ref={j3} {...segmentProps}>
                    <BallCollider args={[0.1]} />
                </RigidBody>

                <RigidBody
                    ref={card}
                    {...segmentProps}
                    mass={2}
                    gravityScale={1.8}
                    type={
                        dragged
                            ? ('kinematicPosition' as RigidBodyProps['type'])
                            : ('dynamic' as RigidBodyProps['type'])
                    }
                >
                    <CuboidCollider args={[0.75, 1.5, 0.02]} />

                    <group
                        position={[0, 0, 0]} // 🔹 centrada
                        onPointerOver={() => hover(true)}
                        onPointerOut={() => hover(false)}
                        onPointerUp={(e: any) => {
                            e.target.releasePointerCapture(e.pointerId);
                            drag(false);
                            card.current.setLinvel({ x: 0, y: 0, z: 0 });
                            card.current.setAngvel({ x: 0, y: 0, z: 0 });
                        }}
                        onPointerDown={(e: any) => {
                            e.target.setPointerCapture(e.pointerId);
                            drag(
                                new THREE.Vector3().copy(e.point).sub(vec.copy(card.current.translation()))
                            );
                        }}
                        // 🔹 mobile support
                        onTouchStart={(e: any) => {
                            drag(new THREE.Vector3(0, 0, 0));
                        }}
                        onTouchEnd={() => drag(false)}
                    >
                        <DynamicCard3D
                            code={code}
                            qrCode={qrCode}
                            eventTitle={eventTitle}
                            areaName={areaName}
                            seatNumber={seatNumber}
                        />
                    </group>
                </RigidBody>
            </group>

            <mesh ref={band}>
                <meshLineGeometry />
                <meshLineMaterial
                    color="white"
                    depthTest={false}
                    resolution={[4096, 4096]}
                    useMap
                    map={texture}
                    repeat={[-10, 1]} // 🔹 menos repeticiones = logo más grande
                    lineWidth={lineWidth}    // 🔹 grosor del cordón
                />
            </mesh>
        </>
    );
}
