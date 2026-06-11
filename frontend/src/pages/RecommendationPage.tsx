import { useEffect, useState } from "react";
import NavBar from "../components/Navbar"
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";
import { useNavigate } from "react-router-dom";
import ErrorPopup from "../components/ErrorPopup";

type RecommendationType = {
    gameId: number;
    title: string;
    coverImageUrl: string;
    averageRating: number;
    score: number;
    reason: string;
};

export default function RecommendationPage() {
    const { token } = useAuth();
    const [recommendations, setRecommendations] = useState<RecommendationType[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const [isErrorPopupOpen, setIsErrorPopupOpen] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        async function loadRecommendations() {
            try {
                const response = await api.get("/recommendations", {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });

                if (!response) {
                    throw new Error("Failed to make recommendations");
                }
                
                setRecommendations(response.data);
            } catch (error: any) {
                setIsErrorPopupOpen(true);
                setMessage(error.message);
            } finally {
                setLoading(false);
            }
        }

        if (token) {
            loadRecommendations();
        }

    }, [token]);
    
    function handleGameDetails(gameId: number) {
        navigate(`/game-details/${gameId}`);
    }

    if (loading) return <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center">Loading...</div>
    if (!recommendations) return <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center">Something went wrong</div>

    return (
        <div className="min-h-screen bg-slate-950 text-slate-100">
            <NavBar />
            <ErrorPopup
                message={message}
                onOk={() => setIsErrorPopupOpen(false)}
                isOpen={isErrorPopupOpen}
            />
            <main className="md:ml-72 px-4 py-8 pt-20 md:px-6 md:py-8 md:pt-8">
                <section className="rounded-3xl border border-slate-800/90 bg-slate-900/95 p-6 shadow-lg shadow-black/20 backdrop-blur-sm">
                    <h1 className="text-3xl font-semibold text-white">Recommendations</h1>
                    <p className="mt-2 text-sm text-slate-400">Personalized game picks based on your library and activity.</p>
                </section>

                <div className="mt-8 grid gap-6 md:grid-cols-2 xl:grid-cols-3">
                    {recommendations.map((recommendation) => (
                        <article
                            key={recommendation.gameId}
                            className="rounded-3xl border border-slate-800/90 bg-slate-900/95 p-5 shadow-xl shadow-black/20 transition duration-200 hover:-translate-y-1 hover:border-sky-500/60"
                            onClick={() => handleGameDetails(recommendation.gameId)}
                            >
                            <div className="relative h-48 overflow-hidden rounded-3xl bg-slate-800">
                                <img className="h-full w-full object-cover" src={recommendation.coverImageUrl} alt={recommendation.title} />
                            </div>
                            <div className="mt-4">
                                <h2 className="text-xl font-semibold text-white">{recommendation.title}</h2>
                                <p className="mt-2 text-sm text-slate-400">Score: {recommendation.score}</p>
                            </div>
                            <div className="mt-4 space-y-2 text-sm text-slate-300">
                                <p>Average rating: {recommendation.averageRating}</p>
                                <p>Reason: {recommendation.reason}</p>
                            </div>
                        </article>
                    ))}
                </div>
            </main>
        </div>
    );
}