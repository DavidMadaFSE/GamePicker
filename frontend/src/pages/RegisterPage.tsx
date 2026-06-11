import api from "../api/axios";
import { type SubmitEventHandler, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function RegisterPage() {
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit: SubmitEventHandler<HTMLFormElement> = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        if (password !== confirmPassword) {
            setError("Passwords do not match");
            setLoading(false);
            return;
        }

        try {
            const response = await api.post("/auth/register", {
                username,
                email,
                password
            });

            console.log("Response " + response);
            navigate("/login");
        } catch (error: any) {
            setError(error.response?.data?.message || "Registeration failed");
        } finally {
            setLoading(false);
        }
    }
    return (
        <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4 py-10">
            <div className="w-full max-w-md rounded-3xl border border-slate-800/80 bg-slate-900/95 p-8 shadow-2xl shadow-black/30 backdrop-blur-sm">
                <div className="mb-8 text-center">
                    <p className="text-sm uppercase tracking-[0.35em] text-emerald-400/80">Create account</p>
                    <h1 className="mt-3 text-3xl font-semibold tracking-tight text-white">Register for NextPlay</h1>
                    <p className="mt-2 text-sm text-slate-400">Get started with your personalized game library and recommendations.</p>
                </div>

                {error ? (
                    <div className="mb-6 rounded-2xl border border-red-500/50 bg-red-500/10 p-4 text-sm text-red-200">
                        {error}
                    </div>
                ) : null}

                <form className="space-y-5" onSubmit={handleSubmit}>
                    <label className="block">
                        <span className="mb-2 block text-sm font-medium text-slate-300">Username</span>
                        <input
                            className="w-full rounded-2xl border border-slate-700/80 bg-slate-950/90 px-4 py-3 text-sm text-white outline-none transition focus:border-emerald-400 focus:ring-2 focus:ring-emerald-400/20"
                            type="text"
                            value={username}
                            onChange={(event) => setUsername(event.target.value)}
                            placeholder="Choose a username"
                            required
                        />
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-sm font-medium text-slate-300">Email</span>
                        <input
                            className="w-full rounded-2xl border border-slate-700/80 bg-slate-950/90 px-4 py-3 text-sm text-white outline-none transition focus:border-emerald-400 focus:ring-2 focus:ring-emerald-400/20"
                            type="email"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                            placeholder="you@example.com"
                            required
                        />
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-sm font-medium text-slate-300">Create Password</span>
                        <input
                            className="w-full rounded-2xl border border-slate-700/80 bg-slate-950/90 px-4 py-3 text-sm text-white outline-none transition focus:border-emerald-400 focus:ring-2 focus:ring-emerald-400/20"
                            type="password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            placeholder="Enter password"
                            required
                        />
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-sm font-medium text-slate-300">Confirm Password</span>
                        <input
                            className="w-full rounded-2xl border border-slate-700/80 bg-slate-950/90 px-4 py-3 text-sm text-white outline-none transition focus:border-emerald-400 focus:ring-2 focus:ring-emerald-400/20"
                            type="password"
                            value={confirmPassword}
                            onChange={(event) => setConfirmPassword(event.target.value)}
                            placeholder="Re-type password"
                            required
                        />
                    </label>

                    <button
                        className="w-full rounded-2xl bg-emerald-500 px-4 py-3 text-sm font-semibold text-slate-950 transition hover:bg-emerald-400 disabled:cursor-not-allowed disabled:bg-slate-700 disabled:text-slate-400"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? "Registering..." : "Create account"}
                    </button>
                </form>

                <p className="mt-6 text-center text-sm text-slate-400">
                    Already have an account?{' '}
                    <Link className="font-semibold text-emerald-300 transition hover:text-emerald-200" to="/login">
                        Login
                    </Link>
                </p>
            </div>
        </div>
    )
}
