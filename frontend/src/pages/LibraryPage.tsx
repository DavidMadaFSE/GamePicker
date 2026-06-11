    import { useEffect, useState } from "react";
import NavBar from "../components/Navbar";
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";
import { useNavigate } from "react-router-dom";
import ErrorPopup from "../components/ErrorPopup";

type LibraryEntryType = {
    libraryEntryId: number;
    gameId: number;
    title: string;
    coverImageUrl: string;
    averageRating: number;
    gameStatus: string;
};

export default function LibraryPage() {
    const navigate = useNavigate();
    const { token } = useAuth();
    const [library, setLibrary] = useState<LibraryEntryType[]>([]);
    const [loading, setLoading] = useState(true);
    const [isErrorPopupOpen, setIsErrorPopupOpen] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        async function loadLibrary() {
            try {
                const response = await api.get("/library");

                if (!response) {
                    throw new Error("Failed to load library");
                }

                console.log(response.data.content);
                setLibrary(response.data.content || []);
            } catch (error: any) {
                setIsErrorPopupOpen(true);
                setMessage(error.message);
            } finally {
                setLoading(false);
            }
        }

        if (token) {
            loadLibrary();
        }
    }, [token]);

    const handleGameDetails = (id: number) => {
        navigate(`/game-details/${id}`);
    }

    if (loading) return <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center"><NavBar />Loading...</div>
    if (!library) return <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center"><NavBar />"No library data</div>

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
                    <h1 className="text-3xl font-semibold text-white">Your Library</h1>
                    <p className="mt-2 text-sm text-slate-400">Quick access to games you&apos;ve saved and tracked.</p>
                </section>

                <div className="mt-8 grid gap-6 md:grid-cols-2 xl:grid-cols-3">
                    {library.map((libraryEntry) => (
                        <article
                            key={libraryEntry.libraryEntryId}
                            className="rounded-3xl border border-slate-800/90 bg-slate-900/95 p-5 shadow-xl shadow-black/20 transition duration-200 hover:-translate-y-1 hover:border-sky-500/60"
                            onClick={() => handleGameDetails(libraryEntry.gameId)}>
                            <div className="relative h-48 overflow-hidden rounded-3xl bg-slate-800">
                                <img className="h-full w-full object-cover" src={libraryEntry.coverImageUrl} alt={libraryEntry.title} />
                            </div>
                            <div className="mt-4">
                                <h2 className="text-xl font-semibold text-white">{libraryEntry.title}</h2>
                                <p className="mt-2 text-sm text-slate-400">Status: {libraryEntry.gameStatus}</p>
                            </div>
                            <div className="mt-4 flex items-center justify-between text-sm text-slate-300">
                                <span className="rounded-2xl bg-slate-800 px-3 py-1">Rating {libraryEntry.averageRating}</span>
                                <span className="text-slate-500">ID {libraryEntry.libraryEntryId}</span>
                            </div>
                        </article>
                    ))}
                </div>
            </main>
        </div>
    );
}