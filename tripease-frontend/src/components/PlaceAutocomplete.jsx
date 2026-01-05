import { useRef, useEffect, useState } from 'react';

export default function PlaceAutocomplete({
    placeholder = 'Enter location',
    onPlaceSelect,
    icon,
    value = ''
}) {
    const inputRef = useRef(null);
    const autocompleteRef = useRef(null);
    const [inputValue, setInputValue] = useState(value);

    useEffect(() => {
        if (!window.google || !inputRef.current) return;

        autocompleteRef.current = new window.google.maps.places.Autocomplete(inputRef.current, {
            componentRestrictions: { country: 'in' },
            fields: ['formatted_address', 'geometry', 'name', 'place_id'],
        });

        autocompleteRef.current.addListener('place_changed', () => {
            const place = autocompleteRef.current.getPlace();
            if (place && place.geometry) {
                setInputValue(place.formatted_address || place.name);
                onPlaceSelect?.({
                    address: place.formatted_address || place.name,
                    lat: place.geometry.location.lat(),
                    lng: place.geometry.location.lng(),
                    placeId: place.place_id,
                });
            }
        });

        return () => {
            if (autocompleteRef.current) {
                window.google.maps.event.clearInstanceListeners(autocompleteRef.current);
            }
        };
    }, [onPlaceSelect]);

    return (
        <div className="relative flex items-center gap-3">
            {icon && (
                <div className="flex-shrink-0">
                    {icon}
                </div>
            )}
            <input
                ref={inputRef}
                type="text"
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder={placeholder}
                className="flex-1 py-3 px-4 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-black bg-white text-gray-900 placeholder-gray-400"
            />
        </div>
    );
}
