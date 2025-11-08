import { useEffect } from 'react';
import { useTexture } from '@react-three/drei';

export function usePreloadLanyardAssets() {
    useEffect(() => {
        // 🔹 Carga anticipada de texturas y modelos usados dentro del Lanyard
        useTexture.preload('/textures/lanyard.png');
        // Si tuvieras modelos GLTF:
        // useGLTF.preload('/models/lanyard.glb');
    }, []);
}
