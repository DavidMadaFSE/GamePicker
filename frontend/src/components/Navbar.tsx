import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import MessagePopup from "./MessagePopup";

export default function NavBar() {
    const { logout, isAuthenticated } = useAuth();
    const [open, setOpen] = useState(false);
    const [isLogin, setIsLogin] = useState(isAuthenticated);
    const [logoutMessage, setLogoutMessage] = useState(false);

    useEffect(() => {
        setIsLogin(isAuthenticated);
    }, [isAuthenticated]);

    const handleLogout = () => {
        logout();
        setOpen(false);
        setLogoutMessage(false);
    };

    return (
        <>
            <MessagePopup
                message="Are you sure you want to logout?"
                onCancel={() => setLogoutMessage(false)}
                onOk={() => handleLogout()}
                isOpen={logoutMessage}
            />
            <div className="fixed inset-x-0 top-0 z-40 flex items-center justify-between gap-4 border-b border-slate-800/90 bg-slate-950/95 px-4 py-3 text-slate-100 backdrop-blur-xl md:hidden">
                <div>
                    <p className="text-xs uppercase tracking-[0.35em] text-sky-400/80">NextPlay</p>
                    <p className="text-lg font-semibold text-white">Game hub</p>
                </div>
                <button
                    className="rounded-2xl border border-slate-700/90 bg-slate-900/95 px-3 py-2 text-sm text-slate-100 transition hover:bg-slate-800"
                    onClick={() => setOpen((prev) => !prev)}
                    type="button"
                >
                    {open ? "Close" : "Menu"}
                </button>
            </div>

            <aside className={`fixed inset-y-0 left-0 z-50 w-72 transform border-r border-slate-800/90 bg-slate-950/95 px-6 py-8 text-slate-100 shadow-xl shadow-black/20 backdrop-blur-xl transition duration-300 ${open ? "translate-x-0" : "-translate-x-full"} md:translate-x-0`}>
                <div className="mb-10">
                    <p className="text-xs uppercase tracking-[0.35em] text-sky-400/80">NextPlay</p>
                    <h1 className="mt-4 text-3xl font-semibold text-white">Game hub</h1>
                    <p className="mt-2 text-sm text-slate-400">Browse, save, and discover new games.</p>
                </div>

                <nav>
                    <ul className="space-y-4 text-sm">
                        <li>
                            <Link className="block rounded-2xl px-4 py-3 transition hover:bg-slate-800/80 hover:text-white" to="/" onClick={() => setOpen(false)}>
                                Games
                            </Link>
                        </li>
                        <li>
                            <Link className="block rounded-2xl px-4 py-3 transition hover:bg-slate-800/80 hover:text-white" to="/library" onClick={() => setOpen(false)}>
                                Library
                            </Link>
                        </li>
                        <li>
                            <Link className="block rounded-2xl px-4 py-3 transition hover:bg-slate-800/80 hover:text-white" to="/recommendations" onClick={() => setOpen(false)}>
                                Recommendations
                            </Link>
                        </li>
                        <li>
                            <Link className="block rounded-2xl px-4 py-3 transition hover:bg-slate-800/80 hover:text-white" to="/profile" onClick={() => setOpen(false)}>
                                Profile
                            </Link>
                        </li>
                        <li>
                            {!isLogin ? (
                                <a
                                    className="block rounded-2xl px-4 py-3 text-emerald-400 transition hover:bg-slate-800/80 hover:text-emerald-300"
                                    href="/login"
                                >
                                    Login/Register
                                </a>
                            ) : (

                                <a
                                    className="block rounded-2xl px-4 py-3 text-rose-300 transition hover:bg-slate-800/80 hover:text-rose-100"
                                    href="/"
                                    onClick={(e) => {
                                        e.preventDefault();
                                        setLogoutMessage(true);
                                        setOpen(false);
                                    }}
                                >
                                    Logout
                                </a>
                            )}
                        </li>
                    </ul>
                </nav>
            </aside>
            {open ? <div className="fixed inset-0 z-40 bg-black/40 md:hidden" onClick={() => setOpen(false)} /> : null}
        </>
    );
}