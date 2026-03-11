import { getAllTools } from '$lib/api';
import type { PageLoad } from './$types';

export const load: PageLoad = async () => {
    const tools = await getAllTools();
    return { tools };
};