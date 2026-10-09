// Usage: node check-icons.mjs <url>...
// Opens each URL in a fresh headless Chrome and reports, for every
// <vaadin-icon> on the page, whether it rendered SVG content.
import { spawn } from 'node:child_process';
import { mkdtempSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

async function check(url) {
  const port = 9300 + Math.floor(Math.random() * 500);
  const profile = mkdtempSync(join(tmpdir(), 'chrome-'));
  const chrome = spawn('google-chrome', ['--headless=new', '--no-sandbox',
    `--remote-debugging-port=${port}`, `--user-data-dir=${profile}`, 'about:blank'],
    { stdio: 'ignore' });
  try {
    let targets;
    for (let i = 0; i < 50; i++) {
      try { targets = await (await fetch(`http://127.0.0.1:${port}/json`)).json(); break; }
      catch { await sleep(200); }
    }
    const page = targets.find((t) => t.type === 'page');
    const ws = new WebSocket(page.webSocketDebuggerUrl);
    await new Promise((r) => ws.addEventListener('open', r));
    let id = 0; const pending = new Map();
    ws.addEventListener('message', (ev) => {
      const msg = JSON.parse(ev.data);
      if (msg.id && pending.has(msg.id)) { pending.get(msg.id)(msg); pending.delete(msg.id); }
    });
    const send = (method, params = {}) => new Promise((r) => {
      const mid = ++id; pending.set(mid, r); ws.send(JSON.stringify({ id: mid, method, params }));
    });
    await send('Page.navigate', { url });
    await sleep(6000);
    const expr = `(() => {
      const icons = [...document.querySelectorAll('vaadin-icon')];
      return JSON.stringify({
        vaadinIconDefined: !!customElements.get('vaadin-icon'),
        loadedChunks: performance.getEntriesByType('resource')
          .map((e) => e.name.split('/').pop())
          .filter((n) => n.startsWith('chunk-'))
          .map((n) => n.slice(0, 18) + '...'),
        icons: icons.map((i) => {
          const group = i.shadowRoot && i.shadowRoot.querySelector('#svg-group');
          return {
            icon: i.getAttribute('icon'),
            rendered: !!group && group.children.length > 0,
          };
        }),
      });
    })()`;
    const res = await send('Runtime.evaluate', { expression: expr, returnByValue: true });
    ws.close();
    return res.result.result.value;
  } finally {
    chrome.kill();
  }
}

for (const url of process.argv.slice(2)) {
  console.log(url, await check(url));
}
