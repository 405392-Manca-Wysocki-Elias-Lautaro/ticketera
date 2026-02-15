// Suprimir warnings específicos de React 19 en desarrollo
if (typeof window !== 'undefined' && process.env.NODE_ENV === 'development') {
  const originalError = console.error;
  const originalWarn = console.warn;

  console.error = (...args) => {
    const message = args[0];
    
    // Suprimir warnings de element.ref de React 19
    if (
      typeof message === 'string' &&
      (
        message.includes('Accessing element.ref was removed in React 19') ||
        message.includes('element.ref is now a regular prop')
      )
    ) {
      return;
    }
    
    originalError.apply(console, args);
  };

  console.warn = (...args) => {
    const message = args[0];
    
    // Suprimir warnings de element.ref de React 19
    if (
      typeof message === 'string' &&
      (
        message.includes('Accessing element.ref was removed in React 19') ||
        message.includes('element.ref is now a regular prop')
      )
    ) {
      return;
    }
    
    originalWarn.apply(console, args);
  };
}
