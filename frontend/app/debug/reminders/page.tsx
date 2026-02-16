'use client';

import { useState } from 'react';
import { reminderService } from '@/services/reminderService'; // Ensure this path is correct
import { Button } from '@/components/ui/button'; // Assuming Shadcn UI
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Loader2 } from 'lucide-react'; // Icons

export default function ReminderDebugPage() {
    const [simulatedTime, setSimulatedTime] = useState<string>('');
    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);

    const handleTrigger = async () => {
        setLoading(true);
        setResult(null);
        setError(null);

        try {
            // If empty, pass undefined to use server time
            const timeToSend = simulatedTime ? new Date(simulatedTime).toISOString() : undefined;
            const message = await reminderService.triggerReminders(timeToSend);
            setResult(message);
        } catch (err: any) {
            console.error(err);
            setError(err.response?.data?.message || err.message || 'Failed to trigger reminders');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container mx-auto p-10 max-w-2xl">
            <Card>
                <CardHeader>
                    <CardTitle>Event Reminder Debugger</CardTitle>
                    <CardDescription>
                        Manually trigger the event reminder job. You can simulate a specific date/time.
                    </CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                    <div className="space-y-2">
                        <Label htmlFor="simulatedTime">Simulated Time (Optional)</Label>
                        <Input
                            id="simulatedTime"
                            type="datetime-local"
                            value={simulatedTime}
                            onChange={(e) => setSimulatedTime(e.target.value)}
                        />
                        <p className="text-sm text-muted-foreground">
                            Leave empty to use current server time.
                        </p>
                    </div>

                    <Button onClick={handleTrigger} disabled={loading} className="w-full">
                        {loading ? (
                            <>
                                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                Triggering...
                            </>
                        ) : (
                            'Trigger Reminders'
                        )}
                    </Button>

                    {result && (
                        <Alert className="bg-green-50 border-green-200 text-green-800">
                            <AlertTitle>Success</AlertTitle>
                            <AlertDescription>{result}</AlertDescription>
                        </Alert>
                    )}

                    {error && (
                        <Alert variant="destructive">
                            <AlertTitle>Error</AlertTitle>
                            <AlertDescription>{error}</AlertDescription>
                        </Alert>
                    )}
                </CardContent>
            </Card>
        </div>
    );
}
