import { Link } from 'react-router-dom';
import Header from '../components/Header';
import Button from '../components/Button';

export default function Landing() {
    return (
        <div className="min-h-screen bg-white">
            <Header />

            <main className="max-w-md mx-auto px-6 py-16">
                <div className="text-center mb-12">
                    <h1 className="text-4xl font-bold text-gray-900 mb-4">
                        Welcome to TripEase
                    </h1>
                    <p className="text-gray-600 text-lg">
                        Book rides or drive with us. Choose how you want to get started.
                    </p>
                </div>

                <div className="space-y-4">
                    {/* Customer Section */}
                    <div className="bg-gray-50 rounded-2xl p-6 border border-gray-100">
                        <div className="flex items-center gap-4 mb-4">
                            <div className="w-12 h-12 bg-black rounded-full flex items-center justify-center">
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                </svg>
                            </div>
                            <div>
                                <h2 className="text-xl font-semibold text-gray-900">Ride with TripEase</h2>
                                <p className="text-gray-500 text-sm">Book auto, bike, or car rides</p>
                            </div>
                        </div>
                        <div className="flex gap-3">
                            <Link to="/customer/login" className="flex-1">
                                <Button fullWidth>Sign In</Button>
                            </Link>
                            <Link to="/customer/register" className="flex-1">
                                <Button variant="secondary" fullWidth>Register</Button>
                            </Link>
                        </div>
                    </div>

                    {/* Divider */}
                    <div className="flex items-center gap-4">
                        <div className="flex-1 h-px bg-gray-200"></div>
                        <span className="text-gray-400 text-sm">or</span>
                        <div className="flex-1 h-px bg-gray-200"></div>
                    </div>

                    {/* Driver Section */}
                    <div className="bg-gray-50 rounded-2xl p-6 border border-gray-100">
                        <div className="flex items-center gap-4 mb-4">
                            <div className="w-12 h-12 bg-black rounded-full flex items-center justify-center">
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
                                </svg>
                            </div>
                            <div>
                                <h2 className="text-xl font-semibold text-gray-900">Drive with TripEase</h2>
                                <p className="text-gray-500 text-sm">Earn money on your schedule</p>
                            </div>
                        </div>
                        <div className="flex gap-3">
                            <Link to="/driver/login" className="flex-1">
                                <Button fullWidth>Sign In</Button>
                            </Link>
                            <Link to="/driver/register" className="flex-1">
                                <Button variant="secondary" fullWidth>Register</Button>
                            </Link>
                        </div>
                    </div>
                </div>

                <p className="text-center text-gray-400 text-xs mt-8">
                    By continuing, you agree to TripEase's Terms of Service and Privacy Policy.
                </p>
            </main>
        </div>
    );
}
