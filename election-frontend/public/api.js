// Simpel: api helpers
// Frontend op 5173, backend op 8080 absolute url
/* eslint-disable @typescript-eslint/no-unused-vars */

const API_BASE = 'http://localhost:8080/api/discussions';

async function apiList() {
    // haalt lijst op
    const res = await fetch(API_BASE);
    if (!res.ok) throw new Error('load fail');
    return res.json();
}

async function apiGet(id) {
    // haalt 1 item op
    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`);
    if (res.status === 404) throw new Error('not found');
    if (!res.ok) throw new Error('load fail');
    return res.json();
}

async function apiCreate(payload) {
    // maakt nieuw item
    const res = await fetch(API_BASE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
    });
    if (res.status === 400) throw new Error('bad input');
    if (!res.ok) throw new Error('save fail');
    return res.json();
}

function fmt(iso) {
    // korte datum
    try { return new Date(iso).toLocaleString(); } catch { return iso; }
}
