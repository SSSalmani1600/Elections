/* Simpele API helper - haalt discussies op van backend */
const API_BASE = 'http://localhost:8080/api/discussions';

/* Haalt lijst met discussies op */
window.apiList = async function apiList() {
    const res = await fetch(API_BASE);
    if (!res.ok) throw new Error(`load fail (${res.status})`);
    return res.json();
};

/* Datum netjes formatteren */
window.fmt = function fmt(iso) {
    try {
        return new Date(iso).toLocaleString();
    } catch {
        return iso;
    }
};
