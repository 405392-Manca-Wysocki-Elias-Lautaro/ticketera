import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import { createBrowserRouter, RouterProvider } from "react-router-dom"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"

import App from "./App.tsx"
import "./index.css"
import PWAUpdater from './components/PWAUpdater.tsx'

const qc = new QueryClient()
const router = createBrowserRouter([
  { path: "/", element: <App /> },
  { path: "/about", element: <div className="p-6">About</div> },
])

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={qc}>
      <RouterProvider router={router} />
      <PWAUpdater />
    </QueryClientProvider>
  </StrictMode>
)
