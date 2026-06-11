import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import NavBar from "../components/Navbar";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import ErrorPopup from "../components/ErrorPopup";
import MessagePopup from "../components/MessagePopup";
import CreateReviewForm from "../components/CreateReviewForm";

type GameDetails = {
    id: number;
    title: string;
    description: string;
    releaseDate: string;
    coverImageUrl: string;
    averageRating: number;
    genres: string[];
    platforms: string[];
}

type Review = {
    id: number;
    gameId: number;
    userId: number;
    username: string;
    rating: number;
    comment: string;
    createdAt: string;
    updatedAt: string;
}

type Library = {
    libraryEntryId: number;
    gameId: number;
    title: string;
    coverImageUrl: string;
    averageRating: number;
    gameStatus: GameStatus;
    createdAt: string;
    updatedAt: string;
}

type GameStatus = "WANT_TO_PLAY" | "PLAYING" | "COMPLETED" | "DROPPED";

export default function GameDetailsPage() {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const [gameDetails, setGameDetails] = useState<GameDetails | null>(null);
    const [reviews, setReviews] = useState<Review[]>([]);
    const [loading, setLoading] = useState(true);
    const [isMessagePopupOpen, setIsMessagePopupOpen] = useState(false);
    const [isErrorPopupOpen, setIsErrorPopupOpen] = useState(false);
    const [isReviewFormOpen, setIsReviewFormOpen] = useState(false);
    const [message, setMessage] = useState("");
    const [alreadyReviewed, setAlreadyReviewed] = useState(false);
    const [alreadyInLibrary, setAlreadyInLibrary] = useState(false);
    const [reviewData, setReviewData] = useState<Review | null>(null);
    const [libraryEntryData, setLibraryEntryData] = useState<Library | null>(null);
    const [gameStatus, setGameStatus] = useState<GameStatus>("WANT_TO_PLAY");
    const [pendingStatus, setPendingStatus] = useState<GameStatus>("WANT_TO_PLAY");
    const [addingGame, setAddingGame] = useState(false);
    const colorMap: Record<GameStatus, string> = {
        WANT_TO_PLAY: "text-sky-500",
        PLAYING: "text-yellow-500",
        COMPLETED: "text-green-500",
        DROPPED: "text-red-500"
    };
    const { id } = useParams<{ id: string }>();

    async function loadGameDetails() {
        if (!id) {
            setIsErrorPopupOpen(true);
            setMessage("Missing game id");
            setLoading(false);
            return;
        }

        try {
            const gameResponse = await api.get<GameDetails>(`/games/${id}`);
            setGameDetails(gameResponse.data);

            const reviewResponse = await api.get<{ content: Review[] }>(`/games/${id}/reviews`);
            setReviews(reviewResponse.data.content || []);
        } catch (error: any) {
            setIsErrorPopupOpen(true);
            setMessage(error.message);
        } finally {
            setLoading(false);
        }
    }

    async function loadUserStatus() {
        if (!id) return;

        const convertedId = parseInt(id, 10);

        try {
            const reviewResponse = await api.get<{ content: Review[] }>("/users/me/reviews");
            const existingReview = reviewResponse.data.content.find((review) => review.gameId === convertedId);

            if (existingReview) {
                setReviewData(existingReview);
                setAlreadyReviewed(true);
            } else {
                setReviewData(null);
                setAlreadyReviewed(false);
            }

            const libraryResponse = await api.get<{ content: Library[] }>("/library");
            const existingLibraryEntry = libraryResponse.data.content.find((entry) => entry.gameId === convertedId);

            if (existingLibraryEntry) {
                setGameStatus(existingLibraryEntry.gameStatus);
                setLibraryEntryData(existingLibraryEntry);
                setAlreadyInLibrary(true);
            } else {
                setLibraryEntryData(null);
                setAlreadyInLibrary(false);
            }
        } catch (error: any) {
            setIsErrorPopupOpen(true);
            setMessage(error.message);
        }
    }

    async function handleRemoveGame() {
        const libraryEntryId = libraryEntryData?.libraryEntryId;
        if (!libraryEntryId) {
            setIsErrorPopupOpen(true);
            setMessage("Missing library entry id");
            return;
        }
        try {
            await api.delete(`/library/${libraryEntryId}`);
            await loadUserStatus();
            setAddingGame(false);
        } catch (error: any) {
            setIsErrorPopupOpen(true);
            setMessage(error.message);
        }
    }

    async function handleAddGame() {
        if (alreadyInLibrary) {
            await handleRemoveGame();
            return;
        }
        try {
            await api.post("/library", {
                gameId: id
            });

            await loadUserStatus();
            setAddingGame(false);
        } catch (error: any) {
            setIsErrorPopupOpen(true);
            setMessage(error.message);
        }
    }

    async function handleUpdateReview(rating: number, comment: string) {
        const reviewId = reviewData?.id;
        if (!reviewId) {
            setIsErrorPopupOpen(true);
            setMessage("Missing review id");
            return;
        }
        try {
            await api.put(`/reviews/${reviewId}`, {
                rating,
                comment
            });

            await loadGameDetails();
            await loadUserStatus();
        } catch (error: any) {
            setIsErrorPopupOpen(true);
            setMessage(error.message);
        }
    }

    async function handleAddReview(rating: number, comment: string) {
        if (alreadyReviewed) {
            await handleUpdateReview(rating, comment);
            return;
        }
        try {
            await api.post(`/games/${id}/reviews`, {
                rating,
                comment
            });

            setAlreadyReviewed(true);
            await loadGameDetails();
            await loadUserStatus();
        } catch (error: any) {
            setIsErrorPopupOpen(true);
            setMessage(error.message);
        }
    }

    const onDropdownChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        if (!gameDetails) return;
        const newValue = e.target.value;
        setPendingStatus(newValue as GameStatus);
        setIsMessagePopupOpen(true);
        setMessage(`Are you sure you want to change status to ${newValue} for ${gameDetails.title}?`)
    }

    async function handleGameStatus() {
        if (!libraryEntryData) return;
        try {
            await api.patch(`/library/${libraryEntryData.libraryEntryId}`, {
                gameStatus: pendingStatus
            });
            setGameStatus(pendingStatus);
        } catch (error: any) {
            setIsErrorPopupOpen(true);
            setMessage(error.message);
        }
    }

    useEffect(() => {
        loadGameDetails();

        if (isAuthenticated && id) {
            loadUserStatus();
        }
    }, [id, isAuthenticated]);

    if (loading) return <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center"><NavBar />Loading...</div>;
    if (!gameDetails) return <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center"><NavBar />No game data</div>;

    return (
        <div className="min-h-screen bg-slate-950 text-slate-100">
            <ErrorPopup
                message={message}
                isOpen={isErrorPopupOpen}
                onOk={() => setIsErrorPopupOpen(false)}
            />
            <MessagePopup
                message={message}
                isOpen={isMessagePopupOpen}
                onCancel={() => setIsMessagePopupOpen(false)}
                onOk={() => {
                    if (addingGame) {
                        handleAddGame();
                    } else {
                        handleGameStatus();
                    }
                    setIsMessagePopupOpen(false);
                }}
            />
            <CreateReviewForm
                onSubmit={(rating, comment) => {
                    if (!comment) {
                        setIsErrorPopupOpen(true);
                        setMessage("Comment section is required");
                    } else {
                        handleAddReview(rating, comment);
                    }
                    setIsReviewFormOpen(false);
                }}
                onCancel={() => setIsReviewFormOpen(false)}
                isOpen={isReviewFormOpen}
                data={reviewData}
            />
            <NavBar />
            <main className="md:ml-72 px-4 py-8 pt-24 md:px-8 md:py-10">
                <div className="mx-auto flex max-w-6xl flex-col items-center gap-10 text-center md:text-left">
                    <section className="w-full rounded-4x-1 border border-slate-800/90 bg-slate-900/95 p-8 shadow-2xl shadow-black/30 backdrop-blur-xl">
                        <div className="flex flex-col items-center gap-5 md:flex-row md:items-end md:justify-between">
                            <div>
                                <p className="text-sm uppercase tracking-[0.35em] text-sky-400/80">Game details</p>
                                <h1 className="mt-3 text-4xl font-semibold text-white sm:text-5xl">{gameDetails.title}</h1>
                                <p className="mt-4 max-w-3xl text-sm text-slate-300 sm:text-base">
                                    {gameDetails.description}
                                </p>

                                <div className="mt-6 flex items-center justify-center md:justify-start gap-3">
                                    {alreadyInLibrary ?
                                        <button
                                            className="rounded-full bg-sky-500 hover:bg-sky-600 hover:text-white px-4 py-2 font-semibold shadow-sm"
                                            onClick={() => {
                                                isAuthenticated ?
                                                    setIsMessagePopupOpen(true) :
                                                    navigate("/login");
                                                setAddingGame(true);
                                                setMessage(`Remove ${gameDetails.title} from your library?`);
                                            }}
                                        >
                                            Remove from library
                                        </button> :
                                        <button
                                            className="rounded-full bg-sky-500 hover:bg-sky-600 hover:text-white px-4 py-2 font-semibold shadow-sm"
                                            onClick={() => {
                                                isAuthenticated ?
                                                    setIsMessagePopupOpen(true) :
                                                    navigate("/login");
                                                setAddingGame(true);
                                                setMessage(`Add ${gameDetails.title} to your library?`);
                                            }}
                                        >
                                            Add to library
                                        </button>
                                    }
                                    {alreadyReviewed ?
                                        <button
                                            className="rounded-full border border-sky-500 text-sky-300 hover:bg-slate-800 px-4 py-2 font-semibold"
                                            onClick={() => {
                                                isAuthenticated ?
                                                    setIsReviewFormOpen(true) :
                                                    navigate("/login");
                                            }}
                                        >
                                            Edit review
                                        </button> :
                                        <button
                                            className="rounded-full border border-sky-500 text-sky-300 hover:bg-slate-800 px-4 py-2 font-semibold"
                                            onClick={() => {
                                                isAuthenticated ?
                                                    setIsReviewFormOpen(true) :
                                                    navigate("/login");
                                            }}
                                        >
                                            Create review
                                        </button>
                                    }
                                    {alreadyInLibrary ?
                                    <select
                                        value={gameStatus}
                                        onChange={onDropdownChange}
                                        className={`rounded-full border border-sky-500 bg-slate-900 hover:bg-slate-800 px-2 py-2 font-semibold ${colorMap[gameStatus]}`}
                                    >
                                        <option className="text-sky-500" value="WANT_TO_PLAY">WANT TO PLAY</option>
                                        <option className="text-yellow-500" value="PLAYING">PLAYING</option>
                                        <option className="text-green-500" value="COMPLETED">COMPLETED</option>
                                        <option className="text-red-500" value="DROPPED">DROPPED</option>
                                    </select> :
                                    null
                                    }
                                </div>
                            </div>
                            <div className="rounded-3xl border border-slate-800/90 bg-slate-950/90 px-5 py-4 shadow-xl shadow-black/20">
                                <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Average rating</p>
                                <p className="mt-3 text-3xl font-semibold text-white">{gameDetails.averageRating.toFixed(1)}</p>
                            </div>
                        </div>
                    </section>

                    <section className="grid w-full gap-6 lg:grid-cols-[360px_1fr]">
                        <div className="rounded-[1.75rem] border border-slate-800/90 bg-slate-950/95 p-6 shadow-xl shadow-black/20 text-center md:text-left">
                            <div className="overflow-hidden rounded-3x-1 bg-slate-800">
                                <img
                                    className="h-72 w-full object-cover"
                                    src={gameDetails.coverImageUrl}
                                    alt={gameDetails.title}
                                />
                            </div>
                            <div className="mt-6 space-y-5">
                                <div>
                                    <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Release date</p>
                                    <p className="mt-2 text-lg font-semibold text-white">{new Date(gameDetails.releaseDate).toLocaleDateString()}</p>
                                </div>
                                <div>
                                    <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Genres</p>
                                    <div className="mt-3 flex flex-wrap justify-center gap-2 md:justify-start">
                                        {gameDetails.genres.map((genre) => (
                                            <span key={genre} className="rounded-full bg-slate-800 px-3 py-1 text-sm text-slate-200 ring-1 ring-slate-700/80">
                                                {genre}
                                            </span>
                                        ))}
                                    </div>
                                </div>
                                <div>
                                    <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Platforms</p>
                                    <div className="mt-3 flex flex-wrap justify-center gap-2 md:justify-start">
                                        {gameDetails.platforms.map((platform) => (
                                            <span key={platform} className="rounded-full bg-slate-800 px-3 py-1 text-sm text-slate-200 ring-1 ring-slate-700/80">
                                                {platform}
                                            </span>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="rounded-[1.75rem] border border-slate-800/90 bg-slate-950/95 p-6 shadow-xl shadow-black/20">
                            <h2 className="text-2xl font-semibold text-white">Details at a glance</h2>
                            <div className="mt-6 grid gap-4 sm:grid-cols-2">
                                <div className="rounded-3xl bg-slate-900/80 p-4 text-left">
                                    <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Title</p>
                                    <p className="mt-2 text-lg font-semibold text-white">{gameDetails.title}</p>
                                </div>
                                <div className="rounded-3xl bg-slate-900/80 p-4 text-left">
                                    <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Rating</p>
                                    <p className="mt-2 text-lg font-semibold text-white">{gameDetails.averageRating} / 5</p>
                                </div>
                                <div className="rounded-3xl bg-slate-900/80 p-4 text-left">
                                    <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Release</p>
                                    <p className="mt-2 text-lg font-semibold text-white">{new Date(gameDetails.releaseDate).toLocaleDateString()}</p>
                                </div>
                                <div className="rounded-3xl bg-slate-900/80 p-4 text-left">
                                    <p className="text-xs uppercase tracking-[0.35em] text-slate-400">Platforms</p>
                                    <p className="mt-2 text-lg font-semibold text-white">{gameDetails.platforms.join(", ")}</p>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section className="w-full rounded-4x-1 border border-slate-800/90 bg-slate-900/95 p-8 shadow-2xl shadow-black/30">
                        <div className="flex flex-col items-center gap-3 text-center">
                            <p className="text-sm uppercase tracking-[0.35em] text-sky-400/80">Community</p>
                            <h2 className="text-3xl font-semibold text-white">Player reviews</h2>
                            <p className="max-w-2xl text-sm text-slate-400">
                                Read what other players say and add your own review when you&apos;re ready.
                            </p>
                        </div>

                        <div className="mt-8 space-y-4">
                            {reviews.length === 0 ? (
                                <div className="rounded-3x-1 border border-dashed border-slate-700/80 bg-slate-950/90 p-8 text-center text-slate-400">
                                    No reviews yet. Be the first to share your thoughts.
                                </div>
                            ) : (
                                reviews.map((review) => (
                                    <div key={review.id} className="rounded-3x-1 border border-slate-800/90 bg-slate-950/95 p-6 shadow-lg shadow-black/10">
                                        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                                            <div>
                                                <p className="text-base font-semibold text-white">{review.username}</p>
                                                <p className="text-sm text-slate-400">{new Date(review.updatedAt).toLocaleDateString()}</p>
                                            </div>
                                            <span className="inline-flex items-center rounded-full bg-slate-800 px-3 py-1 text-sm font-semibold text-sky-300">
                                                {review.rating} / 5
                                            </span>
                                        </div>
                                        <p className="mt-4 text-sm leading-6 text-slate-300">{review.comment}</p>
                                    </div>
                                ))
                            )}
                        </div>
                    </section>
                </div>
            </main>
        </div>
    );
}