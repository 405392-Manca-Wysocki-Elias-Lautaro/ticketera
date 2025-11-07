import React, { useEffect, useRef, useState } from 'react';

type StarBorderProps<T extends React.ElementType> = React.ComponentPropsWithoutRef<T> & {
    as?: T;
    className?: string;
    children?: React.ReactNode;
    color?: string;
    speed?: React.CSSProperties['animationDuration'];
    thickness?: number;
};

const StarBorder = <T extends React.ElementType = 'div'>({
    as,
    className = '',
    color = 'var(--color-accent)',
    speed = '3s',
    thickness = 2,
    children,
    ...rest
}: StarBorderProps<T>) => {
    const Component = as || 'div';
    const ref = useRef<HTMLDivElement>(null);
    const [isDisabled, setIsDisabled] = useState(false);

    useEffect(() => {
        if (!ref.current) return;
        const button = ref.current.querySelector('button');
        if (button) {
            setIsDisabled(button.disabled);
            // por si cambia dinámicamente
            const observer = new MutationObserver(() => setIsDisabled(button.disabled));
            observer.observe(button, { attributes: true, attributeFilter: ['disabled'] });
            return () => observer.disconnect();
        }
    }, []);

    return (
        <Component
            ref={ref}
            className={`relative inline-block overflow-hidden rounded-md ${className}`}
            {...(rest as any)}
            style={{
                padding: `${thickness}px 0`,
                ...(rest as any).style
            }}
        >
            {!isDisabled && (
                <>
                    <div
                        className="absolute w-[300%] h-[50%] opacity-70 bottom-[-11px] right-[-250%] rounded-full animate-star-movement-bottom pointer-events-none z-0"
                        style={{
                            background: `radial-gradient(circle, ${color}, transparent 10%)`,
                            animationDuration: speed
                        }}
                    />
                    <div
                        className="absolute w-[300%] h-[50%] opacity-70 top-[-10px] left-[-250%] rounded-full animate-star-movement-top pointer-events-none z-0"
                        style={{
                            background: `radial-gradient(circle, ${color}, transparent 10%)`,
                            animationDuration: speed
                        }}
                    />
                </>
            )}
            <div className="relative z-10 bg-transparent">{children}</div>
        </Component>
    );
};

export default StarBorder;
