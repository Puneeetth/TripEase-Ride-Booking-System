import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Button from '../components/Button';
import Input from '../components/Input';

export default function DriverDocumentVerification() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        driverLicenseNumber: '',
        expiryDate: '',
        aadhaarNumber: '',
        panCardNumber: '',
    });
    const [error, setError] = useState('');

    const user = JSON.parse(localStorage.getItem('user') || '{}');

    const handleChange = (e) => {
        const { name, value } = e.target;
        let formattedValue = value;

        // Auto-uppercase for license and PAN
        if (name === 'driverLicenseNumber' || name === 'panCardNumber') {
            formattedValue = value.toUpperCase();
        }

        setFormData({ ...formData, [name]: formattedValue });
        setError('');
    };

    const validateForm = () => {
        const licensePattern = /^[A-Z]{2}[0-9]{13}$/;
        const aadhaarPattern = /^[2-9]{1}[0-9]{11}$/;
        const panPattern = /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/;

        if (!licensePattern.test(formData.driverLicenseNumber)) {
            setError('Invalid Driving License Number. Format: 2 letters followed by 13 digits (e.g., DL1234567890123)');
            return false;
        }

        if (!formData.expiryDate) {
            setError('Please enter the license expiry date');
            return false;
        }

        if (!aadhaarPattern.test(formData.aadhaarNumber)) {
            setError('Invalid Aadhaar Number. Must be 12 digits starting with 2-9');
            return false;
        }

        if (!panPattern.test(formData.panCardNumber)) {
            setError('Invalid PAN Number. Format: 5 letters, 4 digits, 1 letter (e.g., ABCDE1234F)');
            return false;
        }

        return true;
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!validateForm()) return;

        // Store document data in localStorage to be submitted with vehicle details
        localStorage.setItem('driverDocuments', JSON.stringify({
            driverLicenseNumber: formData.driverLicenseNumber,
            expiryDate: formData.expiryDate,
            aadhaarNumber: formData.aadhaarNumber,
            panCardNumber: formData.panCardNumber,
        }));

        // Navigate to vehicle details page
        navigate('/driver/vehicle');
    };

    return (
        <div className="min-h-screen bg-white">
            <Header showBack backTo="/driver/register" />

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
                            <span className="text-white text-sm font-semibold">2</span>
                        </div>
                        <span className="ml-2 text-sm text-gray-900 font-medium">Documents</span>
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
                        Document Verification
                    </h1>
                    <p className="text-gray-600">
                        Please provide your official documents for verification
                    </p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <Input
                        label="Driving License Number"
                        type="text"
                        name="driverLicenseNumber"
                        value={formData.driverLicenseNumber}
                        onChange={handleChange}
                        placeholder="e.g., DL1234567890123"
                        maxLength={15}
                        required
                    />
                    <p className="text-xs text-gray-500 -mt-2">Format: 2 letters + 13 digits</p>

                    <Input
                        label="License Expiry Date"
                        type="date"
                        name="expiryDate"
                        value={formData.expiryDate}
                        onChange={handleChange}
                        min={new Date().toISOString().split('T')[0]}
                        required
                    />

                    <Input
                        label="Aadhaar Number"
                        type="text"
                        name="aadhaarNumber"
                        value={formData.aadhaarNumber}
                        onChange={handleChange}
                        placeholder="e.g., 234567890123"
                        maxLength={12}
                        required
                    />
                    <p className="text-xs text-gray-500 -mt-2">12-digit Aadhaar number (starts with 2-9)</p>

                    <Input
                        label="PAN Card Number"
                        type="text"
                        name="panCardNumber"
                        value={formData.panCardNumber}
                        onChange={handleChange}
                        placeholder="e.g., ABCDE1234F"
                        maxLength={10}
                        required
                    />
                    <p className="text-xs text-gray-500 -mt-2">Format: 5 letters + 4 digits + 1 letter</p>

                    {error && (
                        <div className="bg-red-50 text-red-600 px-4 py-3 rounded-lg text-sm">
                            {error}
                        </div>
                    )}

                    <Button
                        type="submit"
                        fullWidth
                        disabled={!formData.driverLicenseNumber || !formData.expiryDate || !formData.aadhaarNumber || !formData.panCardNumber}
                    >
                        Next: Vehicle Details
                    </Button>
                </form>

                <p className="text-center text-gray-400 text-xs mt-6">
                    Your documents will be verified within 24-48 hours. You can still access the dashboard while verification is pending.
                </p>
            </main>
        </div>
    );
}
