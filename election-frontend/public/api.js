/* eslint-env browser */

// Simpel: base url naar backend api (poort 8080)
const API_BASE = 'http://localhost:8080/api/discussions';

// Haal token (als je die hebt) uit localStorage
function getAuthHeaders() {
    const token = localStorage.getItem('authToken'); // zet via console: localStorage.setItem('authToken','DEV_OR_JWT')
    return token ? { Authorization: `Bearer ${token}` } : {};
}

// ---------- Globale helpers (beschikbaar als apiList(), apiGet(), apiCreate(), fmt()) ----------

// Lijst ophalen
window.apiList = async function apiList() {
    const res = await fetch(API_BASE, {
        headers: getAuthHeaders(),
    });
    if (res.status === 401 || res.status === 403) throw new Error('unauthorized');
    if (!res.ok) throw new Error('load fail');
    return res.json();
};

// Detail ophalen
window.apiGet = async function apiGet(id) {
    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, {
        headers: getAuthHeaders(),
    });
    if (res.status === 404) throw new Error('not found');
    if (res.status === 401 || res.status === 403) throw new Error('unauthorized');
    if (!res.ok) throw new Error('load fail');
    return res.json();
};

// Nieuwe discussie maken
window.apiCreate = async function apiCreate(payload) {
    const res = await fetch(API_BASE, {
        method: 'POST',
        headers: {
            ...getAuthHeaders(),
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
    });
    if (res.status === 400) throw new Error('bad input');
    if (res.status === 401 || res.status === 403) throw new Error('unauthorized');
    if (!res.ok) throw new Error('save fail');
    return res.json();
};

// Datum formatteren
window.fmt = function fmt(iso) {
    try {
        return new Date(iso).toLocaleString();
    } catch {
        return iso;
    }
};
