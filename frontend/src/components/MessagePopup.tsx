type MessagePopup = {
    message: string;
    onCancel: () => void;
    onOk: () => void;
    isOpen: boolean;
};

export default function MessagePopup({ message, onCancel, onOk, isOpen }: MessagePopup) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/60 backdrop-blur-sm">
            <div className="absolute inset-0 bg-slate-950/70" />
            <div className="relative mx-4 w-full max-w-lg rounded-4x-1 border border-slate-700/90 bg-slate-900/95 p-6 shadow-2xl shadow-black/40">
                <div className="space-y-4 text-center">
                    <h2 className="text-xl font-semibold text-white">Confirm action</h2>
                    <p className="text-sm leading-7 text-slate-300">{message}</p>
                </div>

                <div className="mt-8 flex flex-col gap-3 sm:flex-row sm:justify-center">
                    <button
                        type="button"
                        onClick={onCancel}
                        className="rounded-full border border-slate-700 bg-slate-950 px-5 py-3 text-sm font-semibold text-slate-200 transition hover:bg-slate-900"
                    >
                        Cancel
                    </button>
                    <button
                        type="button"
                        onClick={onOk}
                        className="rounded-full bg-sky-500 px-5 py-3 text-sm font-semibold text-white shadow-sm shadow-sky-500/20 transition hover:bg-sky-600"
                    >
                        Ok
                    </button>
                </div>
            </div>
        </div>
    );
}
