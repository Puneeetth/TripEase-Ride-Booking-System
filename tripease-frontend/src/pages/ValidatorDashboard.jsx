import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Button from '../components/Button';
import { validatorAPI } from '../services/api';

export default function ValidatorDashboard() {
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [pendingDocs, setPendingDocs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(null);
    const [rejectReason, setRejectReason] = useState('');
    const [rejectingId, setRejectingId] = useState(null);

    useEffect(() => {
        const userData = localStorage.getItem('user');
        if (!userData) {
            navigate('/validator/login');
            return;
        }
        const parsed = JSON.parse(userData);
        if (parsed.role !== 'VALIDATOR') {
            navigate('/validator/login');
            return;
        }
        setUser(parsed);
        fetchPendingDocuments();
    }, [navigate]);

    const fetchPendingDocuments = async () => {
        try {
            const response = await validatorAPI.getPendingDocuments();
            setPendingDocs(response.data);
        } catch (err) {
            console.error('Error fetching pending documents:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        navigate('/validator/login');
    };

    const handleApprove = async (driverId) => {
        setActionLoading(driverId);
        try {
            await validatorAPI.approveDocument(driverId);
            fetchPendingDocuments();
        } catch (err) {
            console.error('Error approving document:', err);
        } finally {
            setActionLoading(null);
        }
    };

    const handleReject = async (driverId) => {
        if (!rejectReason.trim()) {
            alert('Please enter a rejection reason');
            return;
        }
        setActionLoading(driverId);
        try {
            await validatorAPI.rejectDocument(driverId, rejectReason);
            setRejectingId(null);
            setRejectReason('');
            fetchPendingDocuments();
        } catch (err) {
            console.error('Error rejecting document:', err);
        } finally {
            setActionLoading(null);
        }
    };

    if (!user) return null;

    return (
        <div className="min-h-screen bg-gray-50">
            <Header />

            <main className="max-w-4xl mx-auto px-6 py-8">
                {/* Header */}
                <div className="bg-white rounded-2xl p-6 shadow-sm mb-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <h1 className="text-2xl font-bold text-gray-900">
                                Document Verification Portal
                            </h1>
                            <p className="text-gray-500">Review and approve driver documents</p>
                        </div>
                        <Button variant="secondary" onClick={handleLogout}>
                            Logout
                        </Button>
                    </div>
                </div>

                {/* Stats */}
                <div className="bg-white rounded-xl p-4 shadow-sm mb-6">
                    <div className="text-center">
                        <div className="text-3xl font-bold text-gray-900">{pendingDocs.length}</div>
                        <div className="text-sm text-gray-500">Pending Verifications</div>
                    </div>
                </div>

                {/* Pending Documents */}
                <div className="bg-white rounded-2xl p-6 shadow-sm">
                    <h2 className="text-lg font-semibold text-gray-900 mb-4">Pending Documents</h2>

                    {loading ? (
                        <div className="text-center py-8 text-gray-400">
                            Loading...
                        </div>
                    ) : pendingDocs.length === 0 ? (
                        <div className="text-center py-8 text-gray-400">
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 mx-auto mb-3 opacity-50" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                            <p>No pending documents to review</p>
                        </div>
                    ) : (
                        <div className="space-y-4">
                            {pendingDocs.map((doc) => (
                                <div key={doc.documentId} className="border border-gray-200 rounded-xl p-4">
                                    <div className="flex justify-between items-start mb-4">
                                        <div>
                                            <h3 className="font-semibold text-gray-900">{doc.driverName}</h3>
                                            <p className="text-sm text-gray-500">{doc.driverEmail}</p>
                                        </div>
                                        <span className="px-3 py-1 bg-yellow-100 text-yellow-700 rounded-full text-sm">
                                            Pending
                                        </span>
                                    </div>

                                    <div className="grid grid-cols-3 gap-4 mb-4 text-sm">
                                        <div>
                                            <div className="text-gray-500">License No.</div>
                                            <div className="font-medium">{doc.driverLicenseNumber}</div>
                                        </div>
                                        <div>
                                            <div className="text-gray-500">Aadhaar</div>
                                            <div className="font-medium">{doc.aadhaarNumber}</div>
                                        </div>
                                        <div>
                                            <div className="text-gray-500">PAN</div>
                                            <div className="font-medium">{doc.panCardNumber}</div>
                                        </div>
                                    </div>

                                    {rejectingId === doc.driverId ? (
                                        <div className="space-y-3">
                                            <textarea
                                                className="w-full p-3 border border-gray-200 rounded-lg text-sm"
                                                placeholder="Enter rejection reason..."
                                                value={rejectReason}
                                                onChange={(e) => setRejectReason(e.target.value)}
                                                rows={2}
                                            />
                                            <div className="flex gap-2">
                                                <Button
                                                    className="!bg-red-600 hover:!bg-red-700"
                                                    onClick={() => handleReject(doc.driverId)}
                                                    loading={actionLoading === doc.driverId}
                                                >
                                                    Confirm Reject
                                                </Button>
                                                <Button
                                                    variant="secondary"
                                                    onClick={() => {
                                                        setRejectingId(null);
                                                        setRejectReason('');
                                                    }}
                                                >
                                                    Cancel
                                                </Button>
                                            </div>
                                        </div>
                                    ) : (
                                        <div className="flex gap-3">
                                            <Button
                                                className="!bg-green-600 hover:!bg-green-700"
                                                onClick={() => handleApprove(doc.driverId)}
                                                loading={actionLoading === doc.driverId}
                                            >
                                                ✓ Approve
                                            </Button>
                                            <Button
                                                variant="secondary"
                                                onClick={() => setRejectingId(doc.driverId)}
                                            >
                                                ✕ Reject
                                            </Button>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}
