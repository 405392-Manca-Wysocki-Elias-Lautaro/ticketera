window._deferredInstallPrompt = null;

window.addEventListener("beforeinstallprompt", (e) => {
    e.preventDefault();
    console.log("👉 PWA beforeinstallprompt capturado GLOBALMENTE");
    window._deferredInstallPrompt = e;
});
