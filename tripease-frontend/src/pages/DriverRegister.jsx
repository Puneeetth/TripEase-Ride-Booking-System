import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Button from '../components/Button';
import Input from '../components/Input';
import { authAPI } from '../services/api';

export default function DriverRegister() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        age: '',
        emailId: '',
        password: '',
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await authAPI.registerDriver({
                name: formData.name,
                age: parseInt(formData.age),
                emailId: formData.emailId,
                password: formData.password,
            });

            if (response.data.token) {
                localStorage.setItem('token', response.data.token);
                localStorage.setItem('user', JSON.stringify(response.data));
                navigate('/driver/documents');
            } else {
                setError(response.data.message || 'Registration failed');
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Something went wrong. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-white">
            <Header showBack backTo="/" />

            <main className="max-w-md mx-auto px-6 py-16">
                {/* Progress Steps */}
                <div className="flex items-center justify-center mb-8">
                    <div className="flex items-center">
                        <div className="w-8 h-8 rounded-full bg-black flex items-center justify-center">
                            <span className="text-white text-sm font-semibold">1</span>
                        </div>
                        <span className="ml-2 text-sm text-gray-900 font-medium">Profile</span>
                    </div>
                    <div className="w-8 h-0.5 bg-gray-300 mx-2"></div>
                    <div className="flex items-center">
                        <div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center">
                            <span className="text-gray-500 text-sm">2</span>
                        </div>
                        <span className="ml-2 text-sm text-gray-400">Documents</span>
                    </div>
                    <div className="w-8 h-0.5 bg-gray-300 mx-2"></div>
                    <div className="flex items-center">
                        <div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center">
                            <span className="text-gray-500 text-sm">3</span>
                        </div>
                        <span className="ml-2 text-sm text-gray-400">Vehicle</span>
                    </div>
                </div>

                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-2">
                        Drive with TripEase
                    </h1>
                    <p className="text-gray-600">
                        Start earning on your own schedule
                    </p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <Input
                        label="Full Name"
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        placeholder="Enter your full name"
                        required
                    />

                    <Input
                        label="Age"
                        type="number"
                        name="age"
                        value={formData.age}
                        onChange={handleChange}
                        placeholder="Your age"
                        required
                    />

                    <Input
                        label="Email"
                        type="email"
                        name="emailId"
                        value={formData.emailId}
                        onChange={handleChange}
                        placeholder="Enter your email"
                        required
                    />

                    <Input
                        label="Password"
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        placeholder="Create a password"
                        required
                    />

                    {error && (
                        <div className="bg-red-50 text-red-600 px-4 py-3 rounded-lg text-sm">
                            {error}
                        </div>
                    )}

                    <Button
                        type="submit"
                        fullWidth
                        loading={loading}
                        disabled={!formData.name || !formData.age || !formData.emailId || !formData.password}
                    >
                        Next: Verify Documents
                    </Button>
                </form>

                <p className="text-center text-gray-400 text-xs mt-6">
                    By signing up, you agree to TripEase's Terms of Service, Driver Agreement, and Privacy Policy.
                </p>

                <p className="text-center text-gray-600 mt-6">
                    Already have a driver account?{' '}
                    <Link to="/driver/login" className="text-black font-medium hover:underline">
                        Sign in
                    </Link>
                </p>
            </main>
        </div>
    );
}
