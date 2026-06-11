import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import { useNavigate } from "react-router-dom";
import ErrorPopup from "../components/ErrorPopup";

type GameType = {
    id: number;
    title: string;
    releaseDate: string;
    coverImageUrl: string;
    averageRating: string;
};

export default function GamesPage() {
    const navigate = useNavigate();
    const [games, setGames] = useState<GameType[]>([]);
    const [searchTitle, setSearchTitle] = useState("");
    const [searchGenre, setSearchGenre] = useState("");
    const [searchPlatform, setSearchPlatform] = useState("");
    const [loading, setLoading] = useState(true);
    const [isErrorPopupOpen, setIsErrorPopupOpen] = useState(false);
    const [message, setMessage] = useState("");

    async function loadGames() {
        setLoading(true);

        try {
            const response = await api.get("/games", {
                params: {
                    title: searchTitle,
                    genre: searchGenre,
                    platform: searchPlatform
                }
            });

            if (!response) {
                throw new Error("Failed to load games");
            }

            setGames(response.data.content);
        } catch (error: any) {
            setIsErrorPopupOpen(true);
            setMessage(error.message);
        } finally {
            setLoading(false);
        }
    }

    const handleGameDetails = (id: number) => {
        navigate(`/game-details/${id}`)
    }

    useEffect(() => {
        loadGames();
    }, []);

    if (loading) return <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center">Loading...</div>
    if (!games) return <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center">No games data</div>

    return (
        <div className="min-h-screen bg-slate-950 text-slate-100">
            <ErrorPopup
                message={message}
                onOk={() => setIsErrorPopupOpen(false)}
                isOpen={isErrorPopupOpen}
            />
            <Navbar />
            <main className="md:ml-72 px-4 py-8 pt-20 md:px-6 md:py-8 md:pt-8">
                <section className="mb-10 flex flex-col gap-4">
                    <div className="rounded-3xl border border-slate-800/90 bg-slate-900/95 p-6 shadow-lg shadow-black/20 backdrop-blur-sm">
                        <h1 className="text-3xl font-semibold text-white">Explore games</h1>
                        <p className="mt-2 max-w-2xl text-sm text-slate-400">Browse the latest titles, filter by genre or platform, and discover your next favorite game.</p>

                        <div className="mt-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
                            <input
                                className="rounded-2xl border border-slate-700/80 bg-slate-950/90 px-4 py-3 text-sm text-white outline-none transition focus:border-sky-400 focus:ring-2 focus:ring-sky-400/20"
                                type="text"
                                value={searchTitle}
                                onChange={(e) => setSearchTitle(e.target.value)}
                                placeholder="Search by title"
                            />
                            <select
                                className="rounded-2xl border border-slate-700/80 bg-slate-950/90 px-4 py-3 text-sm text-white outline-none transition focus:border-sky-400 focus:ring-2 focus:ring-sky-400/20"
                                value={searchGenre}
                                onChange={(e) => setSearchGenre(e.target.value)}
                            >
                                <option value="">Select genre</option>
                                <option value="ACTION">Action</option>
                                <option value="ADVENTURE">Adventure</option>
                                <option value="RPG">RPG</option>
                                <option value="SHOOTER">Shooter</option>
                                <option value="SPORTS">Sports</option>
                                <option value="RACING">Racing</option>
                                <option value="PUZZLE">Puzzle</option>
                                <option value="HORROR">Horror</option>
                                <option value="STRATEGY">Strategy</option>
                                <option value="SIMULATION">Simulation</option>
                            </select>
                            <select
                                className="rounded-2xl border border-slate-700/80 bg-slate-950/90 px-4 py-3 text-sm text-white outline-none transition focus:border-sky-400 focus:ring-2 focus:ring-sky-400/20"
                                value={searchPlatform}
                                onChange={(e) => setSearchPlatform(e.target.value)}
                            >
                                <option value="">Select platform</option>
                                <option value="PC">PC</option>
                                <option value="PLAYSTATION">Playstation</option>
                                <option value="XBOX">Xbox</option>
                                <option value="NINTENDO_SWITCH">Nintendo Switch</option>
                                <option value="MOBILE">Mobile</option>
                            </select>
                        </div>
                        <button
                            className="mt-4 inline-flex rounded-2xl bg-sky-500 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-sky-400"
                            type="button"
                            onClick={loadGames}
                        >
                            Search
                        </button>
                    </div>
                </section>

                <section>
                    <div className="flex items-center justify-between gap-4">
                        <div>
                            <h2 className="text-2xl font-semibold text-white">Featured games</h2>
                            <p className="mt-1 text-sm text-slate-400">Swipe through the latest game picks.</p>
                        </div>
                        <span className="text-sm text-slate-500">{games.length} titles</span>
                    </div>

                    <div className="mt-6 overflow-x-auto pb-6">
                        <div className="flex gap-6 min-w-max">
                            {games.map((game) => (
                                <article
                                    key={game.id}
                                    className="min-w-70 max-w-[320px] rounded-3xl border border-slate-800/90 bg-slate-900/95 p-5 shadow-xl shadow-black/20 transition duration-200 hover:-translate-y-1 hover:border-sky-500/60"
                                    onClick={() => handleGameDetails(game.id)}>
                                    <div className="relative h-48 overflow-hidden rounded-3xl bg-slate-800">
                                        <img
                                            className="h-full w-full object-cover"
                                            src={game.coverImageUrl}
                                            alt={game.title}
                                        />
                                    </div>
                                    <div className="mt-4">
                                        <h3 className="text-lg font-semibold text-white">{game.title}</h3>
                                        <p className="mt-2 text-sm text-slate-400">Release: {game.releaseDate}</p>
                                    </div>
                                    <div className="mt-4 flex items-center justify-between text-sm text-slate-300">
                                        <span className="rounded-2xl bg-slate-800 px-3 py-1">Rating {game.averageRating}</span>
                                        <span className="text-slate-500">ID #{game.id}</span>
                                    </div>
                                </article>
                            ))}
                        </div>
                    </div>
                </section>
            </main>
        </div>
    );
}