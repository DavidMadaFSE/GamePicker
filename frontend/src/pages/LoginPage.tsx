import api from "../api/axios"
import { type SubmitEventHandler, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit: SubmitEventHandler<HTMLFormElement> = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const response = await api.post("/auth/login", {
                email,
                password,
            });

            login(response.data.token);
            navigate("/");
        } catch (error: any) {
            setError(error.response?.data?.message || "Login failed");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4 py-10">
            <div className="w-full max-w-md rounded-3xl border border-slate-800/80 bg-slate-900/95 p-8 shadow-2xl shadow-black/30 backdrop-blur-sm">
                <div className="mb-8 text-center">
                    <p className="text-sm uppercase tracking-[0.35em] text-sky-400/80">Welcome back</p>
                    <h1 className="mt-3 text-3xl font-semibold tracking-tight text-white">Sign in to your account</h1>
                    <p className="mt-2 text-sm text-slate-400">Access your library, recommendations, and profile.</p>
                </div>

                {error ? (
                    <div className="mb-6 rounded-2xl border border-red-500/50 bg-red-500/10 p-4 text-sm text-red-200">
                        {error}
                    </div>
                ) : null}

                <form className="space-y-5" onSubmit={handleSubmit}>
                    <label className="block">
                        <span className="mb-2 block text-sm font-medium text-slate-300">Email</span>
                        <input
                            className="w-full rounded-2xl border border-slate-700/80 bg-slate-950/90 px-4 py-3 text-sm text-white outline-none transition focus:border-sky-400 focus:ring-2 focus:ring-sky-400/20"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="you@example.com"
                            required
                        />
                    </label>

                    <label className="block">
                        <span className="mb-2 block text-sm font-medium text-slate-300">Password</span>
                        <input
                            className="w-full rounded-2xl border border-slate-700/80 bg-slate-950/90 px-4 py-3 text-sm text-white outline-none transition focus:border-sky-400 focus:ring-2 focus:ring-sky-400/20"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Enter your password"
                            required
                        />
                    </label>

                    <button
                        className="w-full rounded-2xl bg-sky-500 px-4 py-3 text-sm font-semibold text-slate-950 transition hover:bg-sky-400 disabled:cursor-not-allowed disabled:bg-slate-700 disabled:text-slate-400"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? "Logging in..." : "Sign In"}
                    </button>
                </form>

                <p className="mt-6 text-center text-sm text-slate-400">
                    Don&apos;t have an account?{' '}
                    <Link className="font-semibold text-sky-300 transition hover:text-sky-200" to="/register">
                        Register
                    </Link>
                </p>
            </div>
        </div>
    );
}