import { getAllConversations, getAllTools } from '$lib/api';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ depends }) => {
    depends('app:conversations');
    const [conversations, registeredTools] = await Promise.all([
        getAllConversations(),
        getAllTools()
    ]);
    const values = Object.values(conversations);
    return {
        conversationCount: values.length,
        messageCount: values.reduce((sum, c) => sum + c.messages.length, 0),
        registeredCount: registeredTools.length
    };
};