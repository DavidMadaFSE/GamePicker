type ErrorPopupProps = {
	message: string;
	onOk: () => void;
	isOpen: boolean;
};

export default function ErrorPopup({ message, onOk, isOpen }: ErrorPopupProps) {
	if (!isOpen) return null;

	return (
		<div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
			<div className="absolute inset-0 bg-black/60" />
			<div className="relative mx-4 w-full max-w-md rounded-2xl border border-red-700/70 bg-red-950/95 p-6 shadow-2xl shadow-red-900/40">
				<div className="flex flex-col items-center gap-4 text-center">
					<div className="flex h-12 w-12 items-center justify-center rounded-full bg-red-700/90 text-white">
						<svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
							<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v4m0 4h.01M10.29 3.86l9.37 16.28A1 1 0 0118.88 22H5.12a1 1 0 01-.78-1.56L13.71 3.86a1 1 0 011.58 0z" />
						</svg>
					</div>
					<h2 className="text-lg font-semibold text-white">Error</h2>
					<p className="text-sm text-red-200">{message}</p>
				</div>

				<div className="mt-6 flex flex-col gap-3 sm:flex-row sm:justify-center">
					<button
						type="button"
						onClick={onOk}
						className="rounded-full bg-red-600 px-5 py-3 text-sm font-semibold text-white shadow-sm shadow-red-600/30 transition hover:bg-red-700"
					>
						Ok
					</button>
				</div>
			</div>
		</div>
	);
}

