import { api } from "../api/client";
import { useEffect, useState } from "react";

export default function Home() {
  const [me, setMe] = useState<any>(null);

  useEffect(() => {
    api.get("/api/users/me").then(r => setMe(r.data)).catch(console.error);
  }, []);

  return (
    <div style={{padding:24, fontFamily:"system-ui"}}>
      <h2>Bem-vinda 👋</h2>
      <pre>{JSON.stringify(me, null, 2)}</pre>
    </div>
  );
}