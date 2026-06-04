
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VaultPro | Unified Security Console</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;600;700;800&display=swap');
        body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #f8fafc; }
        .sidebar { background: #0f172a; transition: transform 0.3s ease-in-out; }
        .active-link { background: #1e293b; border-left: 4px solid #3b82f6; color: white !important; }
        .card-shadow { box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.04); }
        .glass-header { background: rgba(255, 255, 255, 0.9); backdrop-filter: blur(12px); }
        .custom-scroll::-webkit-scrollbar { width: 5px; }
        .custom-scroll::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
        .page-section { animation: fadeIn 0.3s ease-in-out; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(5px); } to { opacity: 1; transform: translateY(0); } }
        
        #sidebarOverlay { display: none; }
        #sidebarOverlay.active { display: block; }
    </style>
</head>
<body class="flex h-screen overflow-hidden">

    <div id="sidebarOverlay" onclick="toggleSidebar()" class="fixed inset-0 bg-slate-900/50 z-40 backdrop-blur-sm"></div>

    <aside id="mainSidebar" class="sidebar w-72 flex flex-col h-full text-slate-400 z-50 shadow-2xl fixed inset-y-0 left-0 transform -translate-x-full lg:relative lg:translate-x-0">
        <div class="p-8 text-white font-extrabold text-2xl flex items-center gap-3">
            <div class="bg-blue-600 w-10 h-10 rounded-xl flex items-center justify-center shadow-lg shadow-blue-500/50">
                <i class="fa-solid fa-shield-halved"></i>
            </div>
            <span>VAULT<span class="text-blue-500 text-xs italic font-black uppercase ml-1">Pro</span></span>
        </div>

        <nav class="flex-1 px-4 space-y-1.5 overflow-y-auto custom-scroll">
            <div class="px-4 py-2 text-[10px] font-black uppercase tracking-[0.2em] text-slate-500">Security Layers</div>
            <button onclick="showSection('my-vault')" id="nav-my-vault" class="nav-item w-full flex items-center gap-4 p-4 rounded-2xl hover:text-white hover:bg-slate-800 transition active-link">
                <i class="fa-solid fa-vault text-lg"></i> <span class="font-bold text-sm">Personal Vault</span>
            </button>
            <button onclick="showSection('incoming')" id="nav-incoming" class="nav-item w-full flex items-center gap-4 p-4 rounded-2xl hover:text-white hover:bg-slate-800 transition">
                <i class="fa-solid fa-inbox text-lg"></i> <span class="font-bold text-sm">Incoming Shares</span>
            </button>
            <button onclick="showSection('community')" id="nav-community" class="nav-item w-full flex items-center gap-4 p-4 rounded-2xl hover:text-white hover:bg-slate-800 transition">
                <i class="fa-solid fa-network-wired text-lg"></i> <span class="font-bold text-sm">Global Nodes</span>
            </button>
            <div class="px-4 py-2 mt-6 text-[10px] font-black uppercase tracking-[0.2em] text-slate-500">Management</div>
            <button onclick="showSection('profile')" id="nav-profile" class="nav-item w-full flex items-center gap-4 p-4 rounded-2xl hover:text-white hover:bg-slate-800 transition">
                <i class="fa-solid fa-user-shield text-lg"></i> <span class="font-bold text-sm">Profile Identity</span>
            </button>
            <button onclick="showSection('audit')" id="nav-audit" class="nav-item w-full flex items-center gap-4 p-4 rounded-2xl hover:text-white hover:bg-slate-800 transition">
                <i class="fa-solid fa-list-check text-lg"></i> <span class="font-bold text-sm">Security Audit</span>
            </button>
        </nav>

        <div class="p-6 mt-auto border-t border-slate-800/50">
            <div class="flex items-center gap-3 mb-6">
                <img id="userPic" src="" class="w-11 h-11 rounded-xl border border-slate-700 bg-slate-800 object-cover">
                <div class="truncate">
                    <p id="userName" class="text-sm font-bold text-white truncate">User</p>
                    <p class="text-[9px] text-emerald-400 font-black tracking-widest uppercase">Node: Active</p>
                </div>
            </div>
            <button onclick="logout()" class="w-full flex items-center gap-4 p-4 text-rose-400 font-bold hover:bg-rose-500/10 rounded-2xl transition text-sm">
                <i class="fa-solid fa-power-off"></i> Terminate Session
            </button>
        </div>
    </aside>

    <div class="flex-1 flex flex-col h-full overflow-hidden">
        <header class="h-20 glass-header border-b flex items-center justify-between px-4 lg:px-10 sticky top-0 z-40">
            <div class="flex items-center gap-4 flex-1">
                <button onclick="toggleSidebar()" class="lg:hidden p-2 text-slate-600 hover:bg-slate-100 rounded-lg">
                    <i class="fa-solid fa-bars-staggered text-xl"></i>
                </button>
                <div class="flex items-center bg-slate-100 px-4 lg:px-6 py-2.5 rounded-2xl border w-full max-w-md">
                    <i class="fa-solid fa-search text-slate-400 mr-3"></i>
                    <input type="text" id="globalSearch" onkeyup="filterVault()" placeholder="Search vault..." class="bg-transparent outline-none text-sm w-full font-medium">
                </div>
            </div>
            <div class="flex items-center gap-4">
                <button onclick="document.getElementById('fileInput').click()" class="bg-slate-900 text-white p-3.5 lg:px-8 rounded-2xl font-black text-xs uppercase shadow-xl hover:scale-105 active:scale-95 transition-all">
                    <i class="fa-solid fa-cloud-arrow-up lg:mr-2"></i> <span class="hidden lg:inline">Secure Upload</span>
                </button>
                <input type="file" id="fileInput" class="hidden" onchange="handleSecureUpload(this)">
            </div>
        </header>

        <main class="p-4 lg:p-10 flex-1 overflow-y-auto custom-scroll bg-slate-50/30">
            
            <section id="sec-my-vault" class="page-section space-y-6 lg:space-y-10">
                <div class="grid grid-cols-1 xl:grid-cols-12 gap-6 lg:gap-10">
                    <div class="xl:col-span-8 bg-white rounded-[1.5rem] lg:rounded-[2.5rem] card-shadow border p-4 lg:p-10 overflow-hidden">
                        <div class="flex justify-between items-center mb-8">
                            <h2 class="text-xl lg:text-2xl font-black text-slate-800">Asset Inventory</h2>
                            <span id="fileCount" class="text-blue-600 font-black">0</span>
                        </div>
                        <div class="overflow-x-auto">
                            <table class="w-full text-left min-w-[600px]">
                                <thead class="text-[10px] font-black uppercase text-slate-400 border-b">
                                    <tr><th class="pb-6">Asset Name</th><th class="pb-6">Integrity Hash</th><th class="pb-6 text-right">Actions</th></tr>
                                </thead>
                                <tbody id="myFilesBody" class="divide-y divide-slate-50"></tbody>
                            </table>
                        </div>
                    </div>
                    <div class="xl:col-span-4 bg-slate-900 rounded-[1.5rem] lg:rounded-[2.5rem] p-6 lg:p-10 text-white shadow-2xl h-fit">
                        <h3 class="font-black text-xl mb-8">Access Control</h3>
                        <div id="requestList" class="space-y-4 max-h-[500px] overflow-y-auto custom-scroll"></div>
                    </div>
                </div>
            </section>

            <section id="sec-profile" class="page-section hidden">
                <div class="max-w-4xl mx-auto bg-white rounded-[2rem] lg:rounded-[3rem] card-shadow border overflow-hidden text-center">
                    <div class="h-32 lg:h-40 bg-gradient-to-r from-blue-700 to-indigo-800"></div>
                    <div class="px-6 lg:px-12 pb-12 -mt-16 flex flex-col items-center">
                        <div class="relative group">
                            <img id="profileLargePic" src="" class="w-32 h-32 lg:w-44 lg:h-44 rounded-[2rem] lg:rounded-[3rem] border-8 border-white shadow-2xl object-cover">
                            <button onclick="document.getElementById('profileInput').click()" class="absolute bottom-1 right-1 bg-slate-900 text-white p-3 rounded-xl shadow-lg hover:scale-110 transition-all"><i class="fa-solid fa-camera"></i></button>
                            <input type="file" id="profileInput" class="hidden" onchange="updateProfilePic(this)">
                        </div>
                        <h2 id="dispProfileName" class="mt-6 text-2xl lg:text-3xl font-black text-slate-800">User Name</h2>
                        <p id="dispProfileEmail" class="text-slate-400 font-bold italic">user@vault.io</p>
                        <div class="w-full grid grid-cols-1 md:grid-cols-2 gap-4 lg:gap-8 mt-12 text-left">
                            <div class="bg-slate-50 p-6 rounded-3xl border border-slate-100">
                                <label class="text-[10px] font-black text-slate-500 uppercase tracking-widest block mb-3">Display Name</label>
                                <input type="text" id="editName" class="w-full bg-white border-2 border-slate-200 rounded-2xl px-5 py-4 font-bold text-slate-700 focus:border-blue-500 outline-none transition">
                            </div>
                            <div class="bg-slate-50 p-6 rounded-3xl border border-slate-100">
                                <label class="text-[10px] font-black text-slate-500 uppercase tracking-widest block mb-3">Login Email</label>
                                <input type="text" id="editEmail" disabled class="w-full bg-slate-100 border-none rounded-2xl px-5 py-4 text-slate-400 font-medium">
                            </div>
                        </div>
                        <button onclick="saveProfileChanges()" class="mt-10 w-full md:w-auto bg-blue-600 text-white px-12 py-4.5 rounded-2xl font-black uppercase tracking-widest shadow-xl shadow-blue-200 hover:bg-blue-700 hover:-translate-y-1 transition-all">Apply Updates</button>
                    </div>
                </div>
            </section>

            <section id="sec-incoming" class="page-section hidden">
                <h2 class="text-2xl font-black text-slate-800 mb-8">Shared With Me</h2>
                <div id="incomingContainer" class="grid grid-cols-1 md:grid-cols-2 gap-6 lg:gap-8"></div>
            </section>

            <section id="sec-community" class="page-section hidden">
                <h2 class="text-2xl lg:text-3xl font-black text-slate-800 mb-8 px-2">Node Discovery</h2>
                <div id="communityGrid" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6 lg:gap-8"></div>
            </section>

            <section id="sec-audit" class="page-section hidden">
                <div class="bg-white rounded-[1.5rem] lg:rounded-[2.5rem] card-shadow border p-6 lg:p-10 overflow-hidden">
                    <h2 class="text-2xl font-black text-slate-800 mb-8">Security Integrity Logs</h2>
                    <div class="overflow-x-auto">
                        <table class="w-full text-left min-w-[600px]">
                            <thead class="bg-slate-50 text-[10px] font-black uppercase text-slate-500">
                                <tr><th class="p-6">Asset Name</th><th class="p-6">Requester</th><th class="p-6">Timestamp</th><th class="p-6">Status</th></tr>
                            </thead>
                            <tbody id="auditBody" class="text-sm"></tbody>
                        </table>
                    </div>
                </div>
            </section>
        </main>
    </div>

    <script>
        const API = "http://localhost:8080/api/users";
        let user = JSON.parse(localStorage.getItem('sessionUser'));

        // --- UI & NAVIGATION ---
        function toggleSidebar() {
            document.getElementById('mainSidebar').classList.toggle('-translate-x-full');
            document.getElementById('sidebarOverlay').classList.toggle('active');
        }

        function showSection(id) {
            document.querySelectorAll('.page-section').forEach(s => s.classList.add('hidden'));
            document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active-link'));
            document.getElementById('sec-' + id).classList.remove('hidden');
            document.getElementById('nav-' + id).classList.add('active-link');
            if(window.innerWidth < 1024) toggleSidebar();
        }

        function getFileTypeMeta(filename) {
            if(!filename) return { icon: 'fa-file', color: 'text-slate-500', bg: 'bg-slate-50' };
            const ext = filename.split('.').pop().toLowerCase();
            const config = {
                pdf: { icon: 'fa-file-pdf', color: 'text-rose-500', bg: 'bg-rose-50' },
                xlsx: { icon: 'fa-file-excel', color: 'text-emerald-500', bg: 'bg-emerald-50' },
                xls: { icon: 'fa-file-excel', color: 'text-emerald-500', bg: 'bg-emerald-50' },
                jpg: { icon: 'fa-file-image', color: 'text-indigo-500', bg: 'bg-indigo-50' },
                png: { icon: 'fa-file-image', color: 'text-indigo-500', bg: 'bg-indigo-50' },
                zip: { icon: 'fa-file-zipper', color: 'text-amber-500', bg: 'bg-amber-50' }
            };
            return config[ext] || { icon: 'fa-file-shield', color: 'text-blue-500', bg: 'bg-blue-50' };
        }

        // --- IDENTITY MANAGEMENT ---
        function syncUserUI() {
            if(!user) return;
            const pic = user.profilePic || `https://ui-avatars.com/api/?name=${user.name}&background=0D8ABC&color=fff`;
            document.getElementById('userName').innerText = user.name;
            document.getElementById('userPic').src = pic;
            document.getElementById('profileLargePic').src = pic;
            document.getElementById('dispProfileName').innerText = user.name;
            document.getElementById('dispProfileEmail').innerText = user.email;
            document.getElementById('editName').value = user.name;
            document.getElementById('editEmail').value = user.email;
        }

        async function updateProfilePic(input) {
            if (input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = async function(e) {
                    const base64Pic = e.target.result;
                    const res = await fetch(`${API}/update-profile-pic`, {
                        method: 'POST', headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ userId: user.id, profilePic: base64Pic })
                    });
                    if(res.ok) {
                        user.profilePic = base64Pic;
                        localStorage.setItem('sessionUser', JSON.stringify(user));
                        syncUserUI();
                        Swal.fire('Identity Updated', 'Photo synced across nodes.', 'success');
                    }
                };
                reader.readAsDataURL(input.files[0]);
            }
        }

        async function saveProfileChanges() {
            const newName = document.getElementById('editName').value;
            const res = await fetch(`${API}/update-profile`, {
                method: 'POST', headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId: user.id, name: newName })
            });
            if(res.ok) {
                user.name = newName;
                localStorage.setItem('sessionUser', JSON.stringify(user));
                syncUserUI();
                Swal.fire('Success', 'Profile identity updated', 'success');
            }
        }

        // --- ASSET MANAGEMENT ---
        async function handleSecureUpload(input) {
            if (!input.files[0] || !user) return;
            const fd = new FormData();
            fd.append('file', input.files[0]); fd.append('userId', user.id);
            Swal.fire({ title: 'Securing Asset...', html: 'Syncing nodes...', didOpen: () => Swal.showLoading() });
            const res = await fetch(`${API}/upload-secure`, { method: 'POST', body: fd });
            if(res.ok) { Swal.fire('Success', 'File Secured', 'success'); loadVault(); }
            input.value = '';
        }

        async function loadVault() {
            const res = await fetch(`${API}/my-files/${user.id}`);
            const files = await res.json();
            document.getElementById('fileCount').innerText = files.length;
            document.getElementById('myFilesBody').innerHTML = files.map(f => {
                const meta = getFileTypeMeta(f.fileName);
                return `<tr class="hover:bg-slate-50 transition-all">
                    <td class="py-6 flex items-center gap-5">
                        <div class="${meta.bg} ${meta.color} w-12 h-12 rounded-2xl flex items-center justify-center"><i class="fa-solid ${meta.icon} text-xl"></i></div>
                        <div><p class="font-black text-slate-800 text-sm truncate max-w-[150px]">${f.fileName}</p></div>
                    </td>
                    <td class="py-6 font-mono text-[10px] text-slate-300">${f.fileHash ? f.fileHash.substring(0,25)+'...' : 'N/A'}</td>
                    <td class="py-6 text-right">
                        <button onclick="sharePrompt(${f.id})" class="p-3 text-slate-400 hover:text-blue-600 transition"><i class="fa-solid fa-paper-plane"></i></button>
                        <button onclick="deleteFile(${f.id})" class="p-3 text-slate-400 hover:text-rose-600 transition"><i class="fa-solid fa-trash-can"></i></button>
                    </td>
                </tr>`;
            }).join('');
        }

        async function sharePrompt(id) {
            const { value: email } = await Swal.fire({ title: 'Direct Share', input: 'email', inputPlaceholder: 'Recipient Email' });
            if(email) {
                await fetch(`${API}/share-file`, { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({email, fileId: id}) });
                Swal.fire('Sent', 'Security key transmitted.', 'success');
                loadRequests();
            }
        }

        async function deleteFile(id) {
            const confirm = await Swal.fire({ title: 'Delete Asset?', text: "Permanent node removal.", icon: 'warning', showCancelButton: true });
            if(confirm.isConfirmed) {
                await fetch(`${API}/delete-file/${id}?userId=${user.id}`, { method: 'DELETE' });
                loadVault();
            }
        }

        async function loadRequests() {
            const res = await fetch(`${API}/pending-requests/${user.id}`);
            const data = await res.json();
            document.getElementById('requestList').innerHTML = data.map(r => `
                <div class="bg-slate-800 p-6 rounded-3xl border border-slate-700/50">
                    <div class="flex justify-between items-start mb-3">
                        <p class="text-[9px] font-black text-blue-400 uppercase">Active Key</p>
                        <button onclick="revokeAccess(${r.id})" class="text-slate-500 hover:text-rose-400"><i class="fa-solid fa-xmark"></i></button>
                    </div>
                    <p class="text-xs font-bold text-white truncate">${r.sharedWithEmail}</p>
                    <div class="bg-slate-950 p-3.5 rounded-2xl flex justify-between items-center mt-4">
                        <span class="text-xl font-black text-emerald-400 tracking-[0.2em] font-mono">${r.shareOtp}</span>
                        <i class="fa-solid fa-key text-xs text-slate-600"></i>
                    </div>
                </div>`).join('');
        }

        async function revokeAccess(id) {
            await fetch(`${API}/revoke-access/${id}`, { method: 'POST' });
            loadRequests();
        }

        // --- GLOBAL & INCOMING ---
        async function loadIncoming() {
            const res = await fetch(`${API}/shared-with-me/${user.email}`);
            const data = await res.json();
            document.getElementById('incomingContainer').innerHTML = data.map(f => `
                <div class="bg-slate-50 p-6 rounded-[2rem] border flex items-center justify-between group">
                    <div class="flex items-center gap-6">
                        <div class="bg-blue-50 text-blue-500 w-14 h-14 rounded-2xl flex items-center justify-center"><i class="fa-solid fa-file-shield text-2xl"></i></div>
                        <div><p class="font-black text-slate-800 text-lg">${f.fileName}</p></div>
                    </div>
                    <button onclick="showSection('community')" class="bg-blue-600 text-white px-8 py-3.5 rounded-2xl text-xs font-black shadow-lg">Unlock</button>
                </div>`).join('');
        }

        async function loadCommunity() {
            const res = await fetch(`${API}/community-files/${user.id}`);
            const data = await res.json();
            document.getElementById('communityGrid').innerHTML = data.map(f => {
                const meta = getFileTypeMeta(f.fileName);
                return `<div class="bg-white p-8 rounded-[2.5rem] border card-shadow group">
                    <div class="flex justify-between mb-8">
                        <div class="${meta.bg} ${meta.color} w-16 h-16 rounded-2xl flex items-center justify-center shadow-sm"><i class="fa-solid ${meta.icon} text-3xl"></i></div>
                        <button onclick="contactOwner('${f.ownerEmail}', '${f.fileName}')" class="text-slate-400 hover:text-blue-600 p-3 rounded-xl bg-slate-50"><i class="fa-solid fa-envelope"></i></button>
                    </div>
                    <h4 class="font-black text-slate-800 text-xl truncate mb-4">${f.fileName}</h4>
                    <button onclick="requestCode(${f.id})" class="w-full bg-slate-100 text-slate-600 py-3 rounded-xl text-[10px] font-black uppercase mb-4">1. Request Key</button>
                    <div class="pt-4 border-t border-slate-100">
                        <input type="text" id="otp-${f.id}" placeholder="Security Key" class="w-full bg-slate-50 border-2 border-slate-200 rounded-xl px-4 py-3 text-xs font-bold text-center mb-2 outline-none">
                        <button onclick="verifyAndDownloadNode(${f.id}, '${f.fileName}')" class="w-full bg-blue-600 text-white py-4 rounded-xl text-[10px] font-black uppercase tracking-widest shadow-lg shadow-blue-200">2. Unlock Asset</button>
                    </div>
                </div>`;
            }).join('');
        }

        async function requestCode(id) {
            await fetch(`${API}/request-access`, {
                method: 'POST', headers: {'Content-Type':'application/json'},
                body: JSON.stringify({ fileId: id, requesterEmail: user.email })
            });
            Swal.fire('Requested', 'Node owner notified.', 'info');
        }

        async function verifyAndDownloadNode(fileId, fileName) {
            const otp = document.getElementById(`otp-${fileId}`).value;
            if (!otp) return Swal.fire('Key Required', 'Enter Security Key.', 'warning');
            const res = await fetch(`${API}/verify-and-download/${fileId}`, {
                method: 'POST', headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ otp: otp, email: user.email })
            });
            if (res.ok) {
                const blob = await res.blob();
                const a = document.createElement('a'); a.href = URL.createObjectURL(blob); a.download = fileName; a.click();
                Swal.fire('Access Granted', 'Download starting...', 'success');
            } else { Swal.fire('Denied', 'Invalid Key.', 'error'); }
        }

        function contactOwner(email, name) { Swal.fire('Node Info', `Owner: ${email}\nAsset: ${name}`, 'info'); }

        async function loadAudit() {
            const res = await fetch(`${API}/audit-logs/${user.id}`);
            const logs = await res.json();
            document.getElementById('auditBody').innerHTML = logs.map(l => `<tr class="border-b text-xs">
                <td class="p-6 font-bold text-slate-700">${l.fileName}</td>
                <td class="p-6 text-blue-600 uppercase font-black">${l.downloaderEmail}</td>
                <td class="p-6 text-slate-400 font-medium">${new Date(l.downloadTime).toLocaleString()}</td>
                <td class="p-6"><span class="bg-emerald-100 text-emerald-700 px-4 py-1.5 rounded-xl text-[10px] font-black uppercase">Cleared</span></td>
            </tr>`).join('');
        }

        function filterVault() {
            const q = document.getElementById('globalSearch').value.toLowerCase();
            document.querySelectorAll('#myFilesBody tr').forEach(row => { 
                row.style.display = row.innerText.toLowerCase().includes(q) ? '' : 'none'; 
            });
        }

        function logout() { localStorage.clear(); window.location.replace("register.html"); }

        window.onload = () => {
            if(!user) window.location.replace("register.html");
            syncUserUI(); loadVault(); loadRequests(); loadAudit(); loadIncoming(); loadCommunity();
        };
    </script>
</body>
</html>