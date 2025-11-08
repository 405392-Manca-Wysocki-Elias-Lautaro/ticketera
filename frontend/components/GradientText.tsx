import React, { ReactNode } from 'react';

interface GradientTextProps {
    children: ReactNode;
    className?: string;
    colors?: string[];
    animationSpeed?: number;
    showBorder?: boolean;
}

export default function GradientText({
    children,
    className = '',
    colors,
    animationSpeed = 3,
    showBorder = false,
}: GradientTextProps) {
    const hasCustomColors = Array.isArray(colors) && colors.length > 0;
    const gradientStyle = hasCustomColors
        ? {
            backgroundImage: `linear-gradient(to right, ${colors.join(', ')})`,
            backgroundSize: '200% 100%',
            backgroundClip: 'text',
            WebkitBackgroundClip: 'text',
            animation: `gradient-flow ${animationSpeed}s linear infinite`,
        }
        : {};

    return (
        <div
            className={`relative mx-auto flex max-w-fit flex-row items-center justify-center rounded-[1.25rem] font-medium backdrop-blur transition-shadow duration-500 ${className}`}
        >
            {showBorder && (
                <div
                    className="absolute inset-0 bg-cover z-0 pointer-events-none gradient-text-animated"
                    style={{
                        animationDuration: `${animationSpeed}s`,
                    }}
                >
                    <div
                        className="absolute inset-0 bg-background rounded-[1.25rem] z-[-1]"
                        style={{
                            width: 'calc(100% - 2px)',
                            height: 'calc(100% - 2px)',
                            left: '50%',
                            top: '50%',
                            transform: 'translate(-50%, -50%)',
                        }}
                    ></div>
                </div>
            )}

            <div
                className={`inline-block relative z-2 text-transparent ${hasCustomColors ? '' : 'gradient-text-animated'
                    }`}
                style={gradientStyle}
            >
                {children}
            </div>
        </div>
    );
}
