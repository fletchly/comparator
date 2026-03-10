import type { Conversation, Tool, WellKnownIds } from '$lib/types';

const BASE_URL = '/api';

async function request<T>(path: string, options?: RequestInit): Promise<T> {
	const response = await fetch(`${BASE_URL}${path}`, options);
	if (!response.ok) {
		throw new Error(`${response.status} ${response.statusText}: ${path}`);
	}
	if (response.status === 204) {
		return undefined as T;
	}
	return response.json();
}

// Conversations

export function getAllConversations(): Promise<Record<string, Conversation>> {
	return request('/conversation');
}

export function clearAllConversations(): Promise<void> {
	return request('/conversation', { method: 'DELETE' });
}

export function getConsoleConversation(): Promise<Conversation> {
	return request('/conversation/console');
}

export function clearConsoleConversation(): Promise<void> {
	return request('/conversation/console', { method: 'DELETE' });
}

export function getChatConversation(): Promise<Conversation> {
	return request('/conversation/chat');
}

export function clearChatConversation(): Promise<void> {
	return request('/conversation/chat', { method: 'DELETE' });
}

export function getPlayerConversations(): Promise<Record<string, Conversation>> {
	return request('/conversation/player');
}

export function getConversation(id: string): Promise<Conversation> {
	return request(`/conversation/${id}`);
}

export function clearConversation(id: string): Promise<void> {
	return request(`/conversation/${id}`, { method: 'DELETE' });
}

// Tools

export function getAllTools(): Promise<Tool[]> {
	return request('/tool');
}

export function getTool(name: string): Promise<Tool> {
	return request(`/tool/${name}`);
}

export function getWellKnownIds(): Promise<WellKnownIds> {
	return request('/conversation/well-known');
}
