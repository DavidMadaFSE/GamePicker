import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";
import NavBar from "../components/Navbar";
import ErrorPopup from "../components/ErrorPopup";

type UserType = {
    username: string;
    email: string;
    role: string;
}

export default function ProfilePage() {
    const { isAuthenticated } = useAuth();
    const [user, setUser] = useState<UserType | null>(null);
    const [loading, setLoading] = useState(true);
    const [isErrorPopupOpen, setIsErrorPopupOpen] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        async function loadProfile() {
            try {
                const response = await api.get("/users/me");

                if (!response) {
                    throw new Error("Failed to load profile");
                }

                const data = await response.data;
                setUser(data);
            } catch(error: any) {
                setIsErrorPopupOpen(true);
                setMessage(error.message);
            } finally {
                setLoading(false);
            }
        }

        if (isAuthenticated) {
            loadProfile();
        }

    }, [isAuthenticated]);

    if (loading) return <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center"><NavBar />Loading...</div>
    if (!user) return <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center"><NavBar />No profile data</div>

    return (
        <div className="min-h-screen bg-slate-950 text-slate-100">
            <ErrorPopup
                message={message}
                onOk={() => setIsErrorPopupOpen(false)}
                isOpen={isErrorPopupOpen}
            />
            <NavBar />
            <main className="md:ml-72 px-4 py-8 pt-20 md:px-6 md:py-8 md:pt-8">
                <section className="rounded-3xl border border-slate-800/90 bg-slate-900/95 p-6 shadow-lg shadow-black/20 backdrop-blur-sm">
                    <h1 className="text-3xl font-semibold text-white">Profile</h1>
                    <p className="mt-2 text-sm text-slate-400">Manage your account details and preferences.</p>
                </section>

                <div className="mt-8 grid gap-6 md:grid-cols-2 xl:grid-cols-3">
                    <section className="rounded-3xl border border-slate-800/90 bg-slate-900/95 p-6 shadow-xl shadow-black/20">
                        <h2 className="text-xl font-semibold text-white">Account details</h2>
                        <div className="mt-4 space-y-3 text-sm text-slate-300">
                            <div>
                                <p className="text-slate-400">Username</p>
                                <p className="mt-1 text-lg text-white">{user.username}</p>
                            </div>
                            <div>
                                <p className="text-slate-400">Email</p>
                                <p className="mt-1 text-lg text-white">{user.email}</p>
                            </div>
                            <div>
                                <p className="text-slate-400">Role</p>
                                <p className="mt-1 text-lg text-white">{user.role}</p>
                            </div>
                        </div>
                    </section>
                </div>
            </main>
        </div>
    );
}