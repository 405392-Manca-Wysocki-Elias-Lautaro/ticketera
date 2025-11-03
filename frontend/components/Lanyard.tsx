'use client';
import { useEffect, useRef, useState } from 'react';
import { extend, invalidate, useFrame } from '@react-three/fiber';
import { useGLTF, useTexture, Environment, Lightformer } from '@react-three/drei';
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


extend({ MeshLineGeometry, MeshLineMaterial });

export default function Lanyard({
    eventTitle,
    areaName,
    seatNumber,
    qrCode,
    ...props
}: {
    eventTitle: string;
    areaName?: string;
    seatNumber?: string;
    qrCode: string;
    scale?: number | [number, number, number];
    position?: [number, number, number];
    rotation?: [number, number, number];
}) {

    return (
        <group {...props}>
            <ambientLight intensity={Math.PI} />
            <Physics gravity={[0, -25, 0]} timeStep={1 / 60}>
                <Band
                    eventTitle={eventTitle}
                    areaName={areaName}
                    seatNumber={seatNumber}
                    qrCode={qrCode}
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
    qrCode: string;
    eventTitle: string;
    areaName?: string;
    seatNumber?: string;
    maxSpeed?: number;
    minSpeed?: number;
}

function Band({
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

    const vec = new THREE.Vector3();
    const ang = new THREE.Vector3();
    const rot = new THREE.Vector3();
    const dir = new THREE.Vector3();

    const segmentProps: any = {
        type: 'dynamic' as RigidBodyProps['type'],
        canSleep: true,
        colliders: false,
        angularDamping: 5,
        linearDamping: 5,
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
                setScale(0.75);
                setCordLength(0.7);
            } else if (w < 1024) {
                setScale(0.95);
                setCordLength(0.85);
            } else {
                setScale(1.2);
                setCordLength(0.9);
            }
        };
        handleResize();
        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    useRopeJoint(fixed, j1, [[0, 0, 0], [0, 0, 0], 0.9]);
    useRopeJoint(j1, j2, [[0, 0, 0], [0, 0, 0], 0.9]);
    useRopeJoint(j2, j3, [[0, 0, 0], [0, 0, 0], 0.9]);
    useSphericalJoint(j3, card, [[0, 0, 0], [0, 0.9, 0]]);
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

    return (
        <>
            <group position={[0, 3.5, 0]} scale={scale}>
                <RigidBody ref={fixed} {...segmentProps} type="fixed" />

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
                            eventTitle={eventTitle}
                            areaName={areaName}
                            seatNumber={seatNumber}
                            qrCode={qrCode}
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
                    repeat={[-8, 1]} // 🔹 menos repeticiones = logo más grande
                    lineWidth={0.5}    // 🔹 grosor del cordón
                />
            </mesh>
        </>
    );
}
