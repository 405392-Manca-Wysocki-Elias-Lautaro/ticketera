export function showBrandConsoleMessage() {
    console.clear();

    const gradient = `
    background: linear-gradient(
      90deg,
      oklch(0.65 0.18 35) 0%,
      oklch(0.6 0.24 350) 33%,
      oklch(0.55 0.25 330) 66%,
      oklch(0.6 0.22 290) 100%
    );
    -webkit-background-clip: text;
    color: transparent;
    font-weight: 800;
    font-family: monospace;
  `;

    console.log(
        `%c
████████╗██╗ ██████╗██╗  ██╗███████╗████████╗██╗     ██╗   ██╗
╚══██╔══╝██║██╔════╝██║ ██╔╝██╔════╝╚══██╔══╝██║     ╚██╗ ██╔╝
   ██║   ██║██║     █████╔╝ █████╗     ██║   ██║      ╚████╔╝ 
   ██║   ██║██║     ██╔═██╗ ██╔══╝     ██║   ██║       ╚██╔╝  
   ██║   ██║╚██████╗██║  ██╗███████╗   ██║   ███████╗   ██║   
   ╚═╝   ╚═╝ ╚═════╝╚═╝  ╚═╝╚══════╝   ╚═╝   ╚══════╝   ╚═╝   
    `,
        gradient
    );

    console.log(
        "%c🎟️  Event & Ticketing Platform — Experience the Future",
        "color: #9ca3af; font-size: 14px; font-family: monospace; font-weight: 600; background: #111; padding: 2px 6px;"
    );

    console.log(
        `%c▶ Ticketly © ${new Date().getFullYear()}`,
        "color: #9ca3af; font-size: 12px; font-family: monospace;"
    );
}
