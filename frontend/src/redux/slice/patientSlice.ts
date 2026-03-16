import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { callFetchPatient } from '@/apis/api';
import { IModelPaginate, IPatient } from '@/types/backend';

interface IState {
    isFetching: boolean;
    meta: {
        page: number;
        pageSize: number;
        pages: number;
        total: number;
    },
    result: IPatient[]
}
// First, create the thunk
export const fetchPatient = createAsyncThunk(
    'patient/fetchPatient',
    async ({ query }: { query: string }) => {
        const response = await callFetchPatient(query);
        return response;
    }
)

const initialState: IState = {
    isFetching: true,
    meta: {
        page: 1,
        pageSize: 10,
        pages: 0,
        total: 0
    },
    result: []
};


export const patientSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        // Use the PayloadAction type to declare the contents of `action.payload`
        setActiveMenu: (state, action) => {
            // state.activeMenu = action.payload;
        },

    },
    extraReducers: (builder) => {
        // Add reducers for additional action types here, and handle loading state as needed
        builder.addCase(fetchPatient.pending, (state, action) => {
            state.isFetching = true;

        })

        builder.addCase(fetchPatient.rejected, (state, action) => {
            state.isFetching = false;

        })

        builder.addCase(fetchPatient.fulfilled, (state, action) => {
            const payload = action.payload;
            if (payload && payload.data) {
                //Cast payload.data as IModelPaginate<IRole> before reading meta/result.
                const pageData = payload.data as unknown as IModelPaginate<IPatient>;
                state.isFetching = false;
                state.meta = pageData.meta;
                state.result = pageData.result;
            }

            // state.courseOrder = action.payload;
        })
    },

});

export const {
    setActiveMenu,
} = patientSlice.actions;

export default patientSlice.reducer;
