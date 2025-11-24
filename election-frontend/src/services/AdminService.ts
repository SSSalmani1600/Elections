export async function getAdminStats() {
  const token = localStorage.getItem("token");

  const res = await fetch("http://localhost:8080/admin/stats", {
    method: "GET",
    headers: {
      "Authorization": "Bearer " + token,
      "Content-Type": "application/json",
    }
  });

  if (!res.ok) {
    console.error("Admin error:", await res.text());
    throw new Error("Not allowed");
  }

  return await res.json();
}
