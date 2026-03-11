import {getAllConversations, getAllTools} from '$lib/api';
import type { PageLoad } from './$types';

export const load: PageLoad = async () => {
    const conversations = await getAllConversations();
    const registeredTools = await getAllTools()
    const registeredCount = registeredTools.length
    const values = Object.values(conversations);
    return {
        conversationCount: values.length,
        messageCount: values.reduce((sum, c) => sum + c.messages.length, 0),
        registeredCount
    };
};