import { Link } from 'react-router-dom';

export default function Header({ showBack = false, backTo = '/' }) {
    return (
        <header className="bg-black text-white py-4 px-6">
            <div className="max-w-7xl mx-auto flex items-center justify-between">
                <div className="flex items-center gap-4">
                    {showBack && (
                        <Link to={backTo} className="text-white hover:text-gray-300 transition-colors">
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                            </svg>
                        </Link>
                    )}
                    <Link to="/" className="text-2xl font-bold tracking-tight">
                        TripEase
                    </Link>
                </div>
            </div>
        </header>
    );
}
