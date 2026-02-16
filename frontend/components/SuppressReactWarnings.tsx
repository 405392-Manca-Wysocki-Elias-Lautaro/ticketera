'use client';

import { useEffect } from 'react';

export function SuppressReactWarnings() {
  useEffect(() => {
    if (process.env.NODE_ENV === 'development') {
      const originalError = console.error;
      const originalWarn = console.warn;

      console.error = (...args: any[]) => {
        const message = args[0];
        
        // Suprimir warnings de element.ref de React 19
        if (
          typeof message === 'string' &&
          (
            message.includes('Accessing element.ref was removed in React 19') ||
            message.includes('element.ref is now a regular prop') ||
            message.includes('ref is now a regular prop')
          )
        ) {
          return;
        }
        
        originalError.apply(console, args);
      };

      console.warn = (...args: any[]) => {
        const message = args[0];
        
        // Suprimir warnings de element.ref de React 19
        if (
          typeof message === 'string' &&
          (
            message.includes('Accessing element.ref was removed in React 19') ||
            message.includes('element.ref is now a regular prop') ||
            message.includes('ref is now a regular prop')
          )
        ) {
          return;
        }
        
        originalWarn.apply(console, args);
      };

      // Cleanup
      return () => {
        console.error = originalError;
        console.warn = originalWarn;
      };
    }
  }, []);

  return null;
}
