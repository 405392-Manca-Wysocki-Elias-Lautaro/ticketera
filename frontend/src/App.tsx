import { Link } from 'react-router-dom'
// @/components/ui/button existe si corriste `shadcn-ui add button`
import { Button } from '@/components/ui/button'

export default function App() {
  return (
    <div className="min-h-dvh grid place-items-center p-6">
      <div className="max-w-xl w-full space-y-4">
        <h1 className="text-3xl font-bold">Hola React + TS</h1>
        <p className="text-muted-foreground">
          Starter con Vite, Router y (opcional) React Query + shadcn/ui.
        </p>
        <div className="flex gap-3">
          <Button asChild><Link to="/about">Ir a About</Link></Button>
          <Button variant="outline" onClick={() => alert('Listo!')}>Probar</Button>
        </div>
      </div>
    </div>
  )
}
