import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Button from '../components/Button';
import Input from '../components/Input';
import { driverDocumentsAPI, driverVehicleAPI } from '../services/api';

export default function DriverVehicleDetails() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        vehicleType: '',
        registrationNumber: '',
        insuranceNumber: '',
        insuranceExpiryDate: '',
        rcNumber: '',
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);

    const user = JSON.parse(localStorage.getItem('user') || '{}');

    const handleChange = (e) => {
        const { name, value } = e.target;
        let formattedValue = value;

        // Auto-uppercase for registration and RC
        if (name === 'registrationNumber' || name === 'rcNumber') {
            formattedValue = value.toUpperCase();
        }

        setFormData({ ...formData, [name]: formattedValue });
        setError('');
    };

    const validateForm = () => {
        const regPattern = /^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$/;

        if (!formData.vehicleType) {
            setError('Please select a vehicle type');
            return false;
        }

        if (!regPattern.test(formData.registrationNumber)) {
            setError('Invalid Registration Number. Format: KA01AB1234');
            return false;
        }

        if (!formData.insuranceNumber) {
            setError('Please enter insurance number');
            return false;
        }

        if (!formData.insuranceExpiryDate) {
            setError('Please enter insurance expiry date');
            return false;
        }

        if (!formData.rcNumber) {
            setError('Please enter RC number');
            return false;
        }

        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        setLoading(true);
        setError('');

        try {
            // Get documents from localStorage (stored in step 2)
            const storedDocs = JSON.parse(localStorage.getItem('driverDocuments') || '{}');

            if (!storedDocs.driverLicenseNumber) {
                setError('Document information missing. Please go back and complete step 2.');
                setLoading(false);
                return;
            }

            // Submit documents first
            const docsResponse = await driverDocumentsAPI.submitDocuments({
                driverId: user.referenceId,
                driverLicenseNumber: storedDocs.driverLicenseNumber,
                expiryDate: storedDocs.expiryDate,
                aadhaarNumber: storedDocs.aadhaarNumber,
                panCardNumber: storedDocs.panCardNumber,
            });

            if (!docsResponse.data.documentId) {
                setError(docsResponse.data.message || 'Document submission failed');
                setLoading(false);
                return;
            }

            // Submit vehicle details
            const vehicleResponse = await driverVehicleAPI.submitVehicleDetails({
                driverId: user.referenceId,
                vehicleType: formData.vehicleType,
                registrationNumber: formData.registrationNumber,
                insuranceNumber: formData.insuranceNumber,
                insuranceExpiryDate: formData.insuranceExpiryDate,
                rcNumber: formData.rcNumber,
            });

            if (vehicleResponse.data.vehicleId) {
                // Clear stored documents
                localStorage.removeItem('driverDocuments');
                setSuccess(true);
            } else {
                setError(vehicleResponse.data.message || 'Vehicle submission failed');
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Something went wrong. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    if (success) {
        return (
            <div className="min-h-screen bg-white">
                <Header />
                <main className="max-w-md mx-auto px-6 py-16 text-center">
                    <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-yellow-100 mb-6">
                        <svg className="w-10 h-10 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">Application Pending</h2>
                    <p className="text-gray-600 mb-6">
                        Thank you for registering! Your application is now under review.
                        We will get back to you as soon as possible once your documents are verified.
                    </p>
                    <p className="text-gray-500 text-sm mb-6">
                        This usually takes 24-48 hours. You will be able to login once approved.
                    </p>
                    <Button onClick={() => navigate('/driver/login')}>Go to Login</Button>
                </main>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-white">
            <Header showBack backTo="/driver/documents" />

            <main className="max-w-md mx-auto px-6 py-16">
                {/* Progress Steps */}
                <div className="flex items-center justify-center mb-8">
                    <div className="flex items-center">
                        <div className="w-8 h-8 rounded-full bg-black flex items-center justify-center">
                            <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                            </svg>
                        </div>
                        <span className="ml-2 text-sm text-gray-900 font-medium">Profile</span>
                    </div>
                    <div className="w-8 h-0.5 bg-black mx-2"></div>
                    <div className="flex items-center">
                        <div className="w-8 h-8 rounded-full bg-black flex items-center justify-center">
                            <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                            </svg>
                        </div>
                        <span className="ml-2 text-sm text-gray-900 font-medium">Documents</span>
                    </div>
                    <div className="w-8 h-0.5 bg-black mx-2"></div>
                    <div className="flex items-center">
                        <div className="w-8 h-8 rounded-full bg-black flex items-center justify-center">
                            <span className="text-white text-sm font-semibold">3</span>
                        </div>
                        <span className="ml-2 text-sm text-gray-900 font-medium">Vehicle</span>
                    </div>
                </div>

                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-2">
                        Vehicle Details
                    </h1>
                    <p className="text-gray-600">
                        Enter your vehicle information
                    </p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Vehicle Type</label>
                        <div className="grid grid-cols-3 gap-3">
                            {['AUTO', 'BIKE', 'CAR'].map((type) => (
                                <button
                                    key={type}
                                    type="button"
                                    onClick={() => setFormData({ ...formData, vehicleType: type })}
                                    className={`p-4 rounded-xl border-2 transition-all ${formData.vehicleType === type
                                        ? 'border-black bg-black text-white'
                                        : 'border-gray-200 hover:border-gray-300'
                                        }`}
                                >
                                    <div className="text-2xl mb-1">
                                        {type === 'AUTO' && '🛺'}
                                        {type === 'BIKE' && '🏍️'}
                                        {type === 'CAR' && '🚗'}
                                    </div>
                                    <div className="text-sm font-medium">{type}</div>
                                </button>
                            ))}
                        </div>
                    </div>

                    <Input
                        label="Registration Number"
                        type="text"
                        name="registrationNumber"
                        value={formData.registrationNumber}
                        onChange={handleChange}
                        placeholder="e.g., KA01AB1234"
                        maxLength={10}
                        required
                    />
                    <p className="text-xs text-gray-500 -mt-2">Format: KA01AB1234</p>

                    <Input
                        label="Insurance Number"
                        type="text"
                        name="insuranceNumber"
                        value={formData.insuranceNumber}
                        onChange={handleChange}
                        placeholder="Enter insurance policy number"
                        required
                    />

                    <Input
                        label="Insurance Expiry Date"
                        type="date"
                        name="insuranceExpiryDate"
                        value={formData.insuranceExpiryDate}
                        onChange={handleChange}
                        min={new Date().toISOString().split('T')[0]}
                        required
                    />

                    <Input
                        label="RC Number"
                        type="text"
                        name="rcNumber"
                        value={formData.rcNumber}
                        onChange={handleChange}
                        placeholder="Enter RC book number"
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
                        disabled={!formData.vehicleType || !formData.registrationNumber || !formData.insuranceNumber || !formData.insuranceExpiryDate || !formData.rcNumber}
                    >
                        Complete Registration
                    </Button>
                </form>

                <p className="text-center text-gray-400 text-xs mt-6">
                    Once submitted, your profile will be reviewed and approved within 24-48 hours.
                </p>
            </main>
        </div>
    );
}
