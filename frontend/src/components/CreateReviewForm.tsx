import { useEffect, useState } from "react";

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

type CreateReviewForm = {
    onSubmit: (rating: number, comment: string) => void;
    onCancel?: () => void;
    isOpen: boolean;
    data: Review | null;
};

export default function CreateReviewForm({ onSubmit, onCancel, isOpen, data }: CreateReviewForm) {
    if (!isOpen) return null;

    const [rating, setRating] = useState(3);
    const [comment, setComment] = useState("");

    useEffect(() => {
        if (data) {
            setRating(data.rating);
            setComment(data.comment);
        }
    }, [data]);

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 backdrop-blur-sm">
            <div className="absolute inset-0 bg-slate-950/70" />
            <div className="relative mx-4 w-full max-w-2xl rounded-4x-1 border border-slate-800/90 bg-slate-950/95 p-6 shadow-2xl shadow-black/40 backdrop-blur-xl">
                <div className="space-y-4">
                    <div>
                        <p className="text-sm uppercase tracking-[0.35em] text-sky-400/80">Write review</p>
                        <h2 className="mt-2 text-3xl font-semibold text-white">Share your rating</h2>
                    </div>

                    <div className="grid gap-4 sm:grid-cols-[220px_1fr]">
                        <label className="flex flex-col gap-2 rounded-3xl border border-slate-800/90 bg-slate-900/90 p-4">
                            <span className="text-sm font-semibold text-slate-200">Rating</span>
                            <input
                                type="number"
                                min={1}
                                max={5}
                                value={rating}
                                onChange={(event) => setRating(Number(event.target.value))}
                                className="rounded-2xl border border-slate-700 bg-slate-950 px-4 py-3 text-lg font-semibold text-white outline-none transition focus:border-sky-500"
                            />
                            <span className="text-xs text-slate-400">Choose a value between 1 and 5.</span>
                        </label>

                        <label className="flex flex-col gap-2 rounded-3xl border border-slate-800/90 bg-slate-900/90 p-4">
                            <span className="text-sm font-semibold text-slate-200">Comment</span>
                            <textarea
                                value={comment}
                                onChange={(event) => setComment(event.target.value)}
                                rows={6}
                                placeholder="Write your thoughts about the game..."
                                className="min-h-45 rounded-2xl border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-slate-100 outline-none transition focus:border-sky-500"
                            />
                        </label>
                    </div>

                    <div className="flex flex-col gap-3 sm:flex-row sm:justify-end">
                        {onCancel ? (
                            <button
                                type="button"
                                onClick={onCancel}
                                className="rounded-full border border-slate-700 bg-slate-950 px-5 py-3 text-sm font-semibold text-slate-200 transition hover:bg-slate-900"
                            >
                                Cancel
                            </button>
                        ) : null}
                        {!data ?
                            <button
                                type="button"
                                onClick={() => onSubmit(rating, comment)}
                                className="rounded-full bg-sky-500 px-5 py-3 text-sm font-semibold text-white shadow-sm shadow-sky-500/20 transition hover:bg-sky-600"
                            >
                                Submit review
                            </button> :
                            <button
                                type="button"
                                onClick={() => onSubmit(rating, comment)}
                                className="rounded-full bg-sky-500 px-5 py-3 text-sm font-semibold text-white shadow-sm shadow-sky-500/20 transition hover:bg-sky-600"
                            >
                                Update Review
                            </button>
                        }
                    </div>
                </div>
            </div>
        </div>
    );
}
